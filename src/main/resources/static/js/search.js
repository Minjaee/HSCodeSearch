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
    <div class="result-item" onclick="openDetail('${item.hsCode}')">

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

function toggleBookmark(hsCode, nameKor, nameEng) {
    if (!isLoggedIn()) {
        alert("로그인이 필요합니다.");
        window.location.href = "/login";
        return;
    }

    let bookmarks = JSON.parse(localStorage.getItem("bookmarks") || "[]");

    const exists = bookmarks.find(b => b.hsCode === hsCode);

    if (exists) {
        // 삭제
        bookmarks = bookmarks.filter(b => b.hsCode !== hsCode);
    } else {
        // 추가
        bookmarks.push({
            hsCode,
            nameKor,
            nameEng
        });
    }

    localStorage.setItem("bookmarks", JSON.stringify(bookmarks));

    refreshBookmarkIcons();
}

function refreshBookmarkIcons() {
    const bookmarks = JSON.parse(localStorage.getItem("bookmarks") || "[]");

    document.querySelectorAll(".result-item").forEach(card => {
        const code = card.querySelector(".result-code").innerText.replace(/\./g, "").trim();
        const icon = card.querySelector(".bookmark-icon");

        const exists = bookmarks.find(b => b.hsCode.replace(/\D/g, "") === code);
        if (exists) icon.classList.add("bookmarked");
        else icon.classList.remove("bookmarked");
    });
}

function openDetail(hsCode) {
    window.location.href = `/detail?code=${hsCode}`;
}