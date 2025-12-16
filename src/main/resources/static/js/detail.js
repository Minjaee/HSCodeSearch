function goMain() {
    window.location.href = "/";
}

function goBack() {
    history.back();
}

const params = new URLSearchParams(window.location.search);
const hsCode = params.get("code");
const tariffMenu = document.getElementById("tariffMenu");
const tariffInfo = document.getElementById("tariffInfo");
let isTariffMenuOpen = false;

let currentHsCodeData = null;

async function loadDetail() {
    const res = await fetch(`/api/detail?code=${encodeURIComponent(hsCode)}`);
    const data = await res.json();

    if (!data) {
        alert("상세 정보를 찾을 수 없습니다.");
        return;
    }

    currentHsCodeData = data;

    // ❗ innerHTML 사용 금지 → 값만 주입
    document.getElementById("hsCode").innerText = format(data.hsCode);
    document.getElementById("nameKor").innerText = data.nameKor ?? "정보 없음";
    document.getElementById("nameEng").innerText = data.nameEng ?? "정보 없음";
    document.getElementById("description").innerText = data.description ?? "설명 없음";
    
    // 관세율 정보 패널에 국문명 표시
    const tariffHint = document.getElementById("tariffPanelHint");
    if (tariffHint) {
        tariffHint.textContent = data.nameKor ?? "정보 없음";
    }

    const btn = document.getElementById("showTariffBtn");
    btn.addEventListener("click", handleTariffClick);

    // History 저장 (비동기로 처리, 실패해도 계속 진행)
    try {
        const params = new URLSearchParams({
            hsCode: data.hsCode,
            nameKor: data.nameKor || "",
            nameEng: data.nameEng || ""
        });
        await fetch("/api/history", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: params.toString()
        });
    } catch (error) {
        console.error("History 저장 실패:", error);
    }

    // 북마크 상태 확인 및 표시
    await refreshBookmarkIcon();
}

loadDetail();

function format(code) {
    if (!code) return "";
    const clean = code.replace(/\D/g, "");
    if (clean.length === 10) return `${clean.slice(0,4)}.${clean.slice(4,6)}.${clean.slice(6)}`;
    if (clean.length === 6) return `${clean.slice(0,4)}.${clean.slice(4,6)}`;
    return clean;
}

function toggleTariffMenu(forceOpen) {
    if (!tariffMenu) return;

    const shouldOpen = forceOpen !== undefined ? forceOpen : !isTariffMenuOpen;

    if (shouldOpen) {
        tariffMenu.style.display = "block";
        // 강제 reflow로 첫 오픈 시에도 슬라이드 애니메이션이 즉시 적용되도록 함
        requestAnimationFrame(() => {
            void tariffMenu.offsetWidth;
            tariffMenu.classList.add("open");
        });
        isTariffMenuOpen = true;
    } else {
        tariffMenu.classList.remove("open");
        isTariffMenuOpen = false;
        setTimeout(() => {
            tariffMenu.style.display = "none";
        }, 350);
    }
}

async function handleTariffClick() {
    if (!hsCode) {
        alert("유효한 HS 코드가 없습니다.");
        return;
    }

    if (isTariffMenuOpen) {
        toggleTariffMenu(false);
        return;
    }

    toggleTariffMenu(true);
    renderTariffLoading();

    try {
        const response = await fetch(`/api/tariff/us/${hsCode}`);
        if (!response.ok) throw new Error();

        const tariff = await response.json();
        renderTariff(tariff);
    } catch (e) {
        renderTariff(null);
    }
}

function renderTariff(tariff) {
    tariffInfo.innerHTML = "";

    if (!tariff) {
        const empty = document.createElement("p");
        empty.className = "tariff-empty";
        empty.textContent = "관세율 정보를 찾을 수 없습니다.";
        tariffInfo.appendChild(empty);
        return;
    }

    addTariffRow("미국 수입 기준 HS Code", format(tariff.usCode));
    addTariffRow("품목명 (미국)", tariff.descEng || "-");
    addTariffRow("기본세율", normalizeRate(tariff.rateGeneral));
    addTariffRow("협정세율", normalizeRate(tariff.rateSpecial));

    if (tariff.rateDuty2) {
        addTariffRow("기타 세율", normalizeRate(tariff.rateDuty2));
    }
}

function addTariffRow(label, value) {
    const wrapper = document.createElement("p");
    const labelEl = document.createElement("span");
    labelEl.className = "tariff-label";
    labelEl.textContent = `${label}:`;

    const valueEl = document.createElement("span");
    valueEl.className = "tariff-value";
    valueEl.textContent = value;

    wrapper.appendChild(labelEl);
    wrapper.appendChild(valueEl);
    tariffInfo.appendChild(wrapper);
}

function normalizeRate(rate) {
    if (rate === null || rate === undefined) return "정보 없음";
    const text = String(rate).trim();
    if (!text) return "정보 없음";
    if (text.toLowerCase() === "free") return "Free";
    return text;
}

function renderTariffLoading() {
    tariffInfo.innerHTML = "";
    const loading = document.createElement("p");
    loading.className = "tariff-empty";
    loading.textContent = "관세율 정보를 불러오는 중...";
    tariffInfo.appendChild(loading);
}

// 북마크 토글 함수
async function toggleBookmark() {
    if (!currentHsCodeData) return;

    // 로그인 확인
    const userRes = await fetch("/api/user");
    if (!userRes.ok || !(await userRes.json())) {
        alert("로그인이 필요합니다.");
        window.location.href = "/login";
        return;
    }

    const hsCode = currentHsCodeData.hsCode;
    const nameKor = currentHsCodeData.nameKor || "";
    const nameEng = currentHsCodeData.nameEng || "";

    // 북마크 존재 여부 확인
    const checkRes = await fetch(`/api/bookmarks/check?hsCode=${encodeURIComponent(hsCode)}`);
    const exists = await checkRes.json();

    try {
        if (exists) {
            // 삭제
            const deleteRes = await fetch(`/api/bookmarks?hsCode=${encodeURIComponent(hsCode)}`, {
                method: "DELETE"
            });
            if (!deleteRes.ok) {
                const errorText = await deleteRes.text();
                console.error("북마크 삭제 실패:", errorText);
                alert("북마크 삭제에 실패했습니다.");
                return;
            }
        } else {
            // 추가
            const params = new URLSearchParams({
                hsCode: hsCode,
                nameKor: nameKor,
                nameEng: nameEng
            });
            const addRes = await fetch("/api/bookmarks", {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded"
                },
                body: params.toString()
            });
            if (!addRes.ok) {
                const errorText = await addRes.text();
                console.error("북마크 추가 실패:", errorText);
                alert(errorText || "북마크 추가에 실패했습니다.");
                return;
            }
        }
        await refreshBookmarkIcon();
    } catch (error) {
        console.error("북마크 토글 실패:", error);
        alert("북마크 작업 중 오류가 발생했습니다.");
    }
}

// 북마크 아이콘 상태 업데이트
async function refreshBookmarkIcon() {
    if (!hsCode) return;

    const icon = document.getElementById("bookmarkIcon");
    if (!icon) return;

    try {
        const res = await fetch(`/api/bookmarks/check?hsCode=${encodeURIComponent(hsCode)}`);
        if (!res.ok) {
            icon.classList.remove("bookmarked");
            return;
        }

        const isBookmarked = await res.json();
        if (isBookmarked) {
            icon.classList.add("bookmarked");
        } else {
            icon.classList.remove("bookmarked");
        }
    } catch (error) {
        console.error("북마크 상태 확인 실패:", error);
        icon.classList.remove("bookmarked");
    }
}
