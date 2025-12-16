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
            const escapedHsCode = escapeHtml(item.hsCode);
            const escapedNameKor = escapeHtml(item.nameKor || "");
            const escapedNameEng = escapeHtml(item.nameEng || "");
            
            container.innerHTML += `
                <div class="result-item" onclick="openDetail('${escapedHsCode}', '${escapedNameKor.replace(/'/g, "\\'")}', '${escapedNameEng.replace(/'/g, "\\'")}')">
                    <span class="delete-bookmark" onclick="event.stopPropagation(); deleteBookmark('${escapedHsCode}')">✕</span>
                    <div class="result-code"><b>${formatHSCode(item.hsCode)}</b></div>
                    <div class="result-name">${escapedNameKor}</div>
                    <div class="result-eng">${escapedNameEng}</div>
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
    try {
        const res = await fetch(`/api/bookmarks?hsCode=${encodeURIComponent(hsCode)}`, {
            method: "DELETE"
        });
        
        if (!res.ok) {
            const errorText = await res.text();
            console.error("북마크 삭제 실패:", errorText);
            alert("북마크 삭제에 실패했습니다.");
            return;
        }

        loadBookmarks();
    } catch (error) {
        console.error("북마크 삭제 실패:", error);
        alert("북마크 삭제 중 오류가 발생했습니다.");
    }
}

// 상세 페이지로 이동
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

// HTML 이스케이프 (XSS 방지)
function escapeHtml(text) {
    const map = {
        '&': '&amp;',
        '<': '&lt;',
        '>': '&gt;',
        '"': '&quot;',
        "'": '&#039;'
    };
    return text.replace(/[&<>"']/g, m => map[m]);
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