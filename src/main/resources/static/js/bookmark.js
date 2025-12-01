// 북마크 로드
function loadBookmarks() {
    const list = JSON.parse(localStorage.getItem("bookmarks") || "[]");
    const container = document.getElementById("bookmarkList");
    const emptyMsg = document.getElementById("emptyMessage");

    container.innerHTML = "";

    if (list.length === 0) {
        emptyMsg.style.display = "block";
        return;
    } else {
        emptyMsg.style.display = "none";
    }

    list.forEach((item, index) => {
        container.innerHTML += `
            <div class="result-item">
                <span class="delete-bookmark" onclick="deleteBookmark(${index})">✕</span>
                <div class="result-code"><b>${formatHSCode(item.hsCode)}</b></div>
                <div class="result-name">${item.nameKor ?? ""}</div>
                <div class="result-eng">${item.nameEng ?? ""}</div>
            </div>
        `;
    });
}

// 북마크 삭제
function deleteBookmark(index) {
    let list = JSON.parse(localStorage.getItem("bookmarks") || "[]");
    list.splice(index, 1);
    localStorage.setItem("bookmarks", JSON.stringify(list));
    loadBookmarks();
}

// HSCode 포맷
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

loadBookmarks();