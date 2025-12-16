// 북마크 로드
async function loadBookmarks() {
    const container = document.getElementById("bookmarkList");
    const emptyMsg = document.getElementById("emptyMessage");

    container.innerHTML = "";

    try {
        const res = await fetch("/api/bookmarks");
        if (!res.ok) {
            if (res.status === 401) {
                alert("로그인이 필요합니다.");
                window.location.href = "/login";
                return;
            }
            throw new Error("북마크를 불러오는데 실패했습니다.");
        }

        const list = await res.json();

        if (list.length === 0) {
            emptyMsg.style.display = "block";
            return;
        } else {
            emptyMsg.style.display = "none";
        }

        list.forEach((item) => {
            container.innerHTML += `
                <div class="result-item" onclick="openDetail('${item.hsCode}')">
                    <span class="delete-bookmark" onclick="event.stopPropagation(); deleteBookmark('${item.hsCode}')">✕</span>
                    <div class="result-code"><b>${formatHSCode(item.hsCode)}</b></div>
                    <div class="result-name">${item.nameKor ?? ""}</div>
                    <div class="result-eng">${item.nameEng ?? ""}</div>
                </div>
            `;
        });
    } catch (error) {
        alert(error.message);
        console.error("북마크 로드 실패:", error);
    }
}

// 북마크 삭제
async function deleteBookmark(hsCode) {
    if (!confirm("북마크를 삭제하시겠습니까?")) {
        return;
    }

    try {
        const res = await fetch(`/api/bookmarks?hsCode=${encodeURIComponent(hsCode)}`, {
            method: "DELETE"
        });
        
        if (!res.ok) {
            throw new Error("북마크 삭제에 실패했습니다.");
        }

        loadBookmarks();
    } catch (error) {
        alert(error.message);
        console.error("북마크 삭제 실패:", error);
    }
}

// 상세 페이지로 이동
function openDetail(hsCode) {
    window.location.href = `/detail?code=${hsCode}`;
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