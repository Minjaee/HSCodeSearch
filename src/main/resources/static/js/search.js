<!-- 검색창 X,Search 버튼 스크립트-->
const input = document.getElementById('searchInput');
const clearBtn = document.getElementById('clearBtn');
const searchBtn = document.getElementById('searchBtn');
const resultsBox = document.getElementById('results');

input.addEventListener('input', () => {
    if (input.value.trim() === "") {
        clearBtn.style.display = "none";
        searchBtn.style.display = "none";
        resultsBox.innerHTML = "";
    } else {
        clearBtn.style.display = "block";
        searchBtn.style.display = "block";
    }
});

clearBtn.addEventListener('click', () => {
    input.value = "";
    clearBtn.style.display = "none";
    searchBtn.style.display = "none";
    resultsBox.innerHTML = "";
    input.focus();
});

async function doSearch() {
    const keyword = input.value.trim();
    if (keyword === "") return;

    resultsBox.innerHTML = "<p>검색 중...</p>";

    const res = await fetch(`/api/search?q=${encodeURIComponent(keyword)}`);
    const data = await res.json();

    if (data.length === 0) {
        resultsBox.innerHTML = `<p>"${keyword}" 검색 결과 없음</p>`;
        return;
    }

    const html = data.map(item => `
    <div class="result-item" onclick="openDetail('${item.hsCode}', '${(item.nameKor ?? "").replace(/'/g, "\\'")}', '${(item.nameEng ?? "").replace(/'/g, "\\'")}')">

            <!-- 북마크 아이콘 -->
           <img src="/img/bookmark_icon.png"
     class="bookmark-icon"
     onclick="event.stopPropagation(); toggleBookmark('${item.hsCode}', '${item.nameKor ?? ""}', '${item.nameEng ?? ""}')">

            <div class="result-code"><b>${formatHSCode(item.hsCode)}</b></div>
            <div class="result-name">${item.nameKor ?? ""}</div>
            <div class="result-eng">${item.nameEng ?? ""}</div>
        </div>
    `).join("");

    resultsBox.innerHTML = html;

    refreshBookmarkIcons();  // 저장된 북마크 표시 업데이트
}

function formatHSCode(code) {
    if (!code) return "";
    const clean = code.replace(/\D/g, "");

    if (clean.length >= 10) {
        return `${clean.slice(0,4)}.${clean.slice(4,6)}.${clean.slice(6,10)}`;
    } else if (clean.length >= 6) {
        return `${clean.slice(0,4)}.${clean.slice(4,6)}`;
    } else if (clean.length >= 4) {
        return clean.slice(0,4);
    }
    return clean;
}

searchBtn.addEventListener('click', doSearch);

input.addEventListener('keydown', (e) => {
    if (e.key === "Enter") doSearch();
});

function isLoggedIn() {
    return localStorage.getItem("loginUser") !== null;
}

async function toggleBookmark(hsCode, nameKor, nameEng) {
    // 로그인 확인
    const userRes = await fetch("/api/user");
    if (!userRes.ok || !(await userRes.json())) {
        alert("로그인이 필요합니다.");
        window.location.href = "/login";
        return;
    }

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
                throw new Error("북마크 삭제에 실패했습니다.");
            }
        } else {
            // 추가
            const params = new URLSearchParams({
                hsCode: hsCode,
                nameKor: nameKor || "",
                nameEng: nameEng || ""
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
                throw new Error(errorText || "북마크 추가에 실패했습니다.");
            }
        }
        refreshBookmarkIcons();
    } catch (error) {
        alert(error.message);
    }
}

async function refreshBookmarkIcons() {
    try {
        const res = await fetch("/api/bookmarks");
        if (!res.ok) {
            // 로그인 안 되어 있으면 모든 북마크 아이콘 제거
            document.querySelectorAll(".bookmark-icon").forEach(icon => {
                icon.classList.remove("bookmarked");
            });
            return;
        }

        const bookmarks = await res.json();

        document.querySelectorAll(".result-item").forEach(card => {
            const codeEl = card.querySelector(".result-code");
            if (!codeEl) return;
            
            const code = codeEl.innerText.replace(/\./g, "").trim();
            const icon = card.querySelector(".bookmark-icon");
            if (!icon) return;

            const exists = bookmarks.find(b => {
                const bookmarkCode = b.hsCode.replace(/\D/g, "");
                return bookmarkCode === code;
            });
            
            if (exists) {
                icon.classList.add("bookmarked");
            } else {
                icon.classList.remove("bookmarked");
            }
        });
    } catch (error) {
        console.error("북마크 상태 업데이트 실패:", error);
    }
}

async function openDetail(hsCode, nameKor = "", nameEng = "") {
    // History 저장 (비동기로 처리, 실패해도 페이지 이동은 계속)
    try {
        const params = new URLSearchParams({
            hsCode: hsCode,
            nameKor: nameKor || "",
            nameEng: nameEng || ""
        });
        await fetch("/api/history", {
            method: "POST",
            headers: {
                "Content-Type": "application/x-www-form-urlencoded"
            },
            body: params.toString()
        });
    } catch (error) {
        // History 저장 실패해도 조용히 처리
        console.error("History 저장 실패:", error);
    }
    
    window.location.href = `/detail?code=${hsCode}`;
}