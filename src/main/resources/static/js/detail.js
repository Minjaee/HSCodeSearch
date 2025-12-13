function goMain() {
    window.location.href = "/";
}

function goBack() {
    history.back();
}

const params = new URLSearchParams(window.location.search);
const hsCode = params.get("code");

async function loadDetail() {
    const res = await fetch(`/api/detail?code=${encodeURIComponent(hsCode)}`);
    const data = await res.json();

    if (!data) {
        alert("상세 정보를 찾을 수 없습니다.");
        return;
    }

    // ❗ innerHTML 사용 금지 → 값만 주입
    document.getElementById("hsCode").innerText = format(data.hsCode);
    document.getElementById("nameKor").innerText = data.nameKor ?? "정보 없음";
    document.getElementById("nameEng").innerText = data.nameEng ?? "정보 없음";
    document.getElementById("description").innerText = data.description ?? "설명 없음";

    const btn = document.getElementById("showTariffBtn");
    const modal = document.getElementById("tariffModal");
    const closeBtn = modal.querySelector(".close");

    btn.addEventListener("click", async () => {
        try {
            const response = await fetch(`/api/us-tariff/${hsCode}`);
            if (!response.ok) throw new Error();

            const tariff = await response.json();

            document.getElementById("tariffContent").innerHTML = `
                <p><b>미국 HS Code:</b> ${format(tariff.usCode)}</p>
                <p><b>영문 품목명:</b> ${tariff.descEng ?? "-"}</p>
                <p><b>일반세율:</b> ${tariff.rateGeneral ?? "free"}</p>
                <p><b>특혜세율:</b> ${tariff.rateSpecial ?? "-"}</p>
                <p><b>비고:</b> ${tariff.note ?? "-"}</p>
            `;

            modal.style.display = "block";
        } catch {
            alert("관세율 정보가 없습니다.");
        }
    });

    closeBtn.onclick = () => modal.style.display = "none";
    window.onclick = e => { if (e.target === modal) modal.style.display = "none"; };
}

loadDetail();

function format(code) {
    if (!code) return "";
    const clean = code.replace(/\D/g, "");
    if (clean.length === 10) return `${clean.slice(0,4)}.${clean.slice(4,6)}.${clean.slice(6)}`;
    if (clean.length === 6) return `${clean.slice(0,4)}.${clean.slice(4,6)}`;
    return clean;
}
