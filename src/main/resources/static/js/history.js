// History 로드
async function loadHistory() {
    const container = document.getElementById("historyList");
    const emptyMsg = document.getElementById("emptyMessage");

    container.innerHTML = "";

    try {
        const res = await fetch("/api/history");
        if (!res.ok) {
            if (res.status === 401) {
                alert("로그인이 필요합니다.");
                window.location.href = "/login";
                return;
            }
            throw new Error("History를 불러오는데 실패했습니다.");
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
                <div class="result-item" onclick="openDetail('${escapedHsCode}')">
                    <img src="/img/bookmark_icon.png"
                         class="bookmark-icon"
                         onclick="event.stopPropagation(); toggleBookmark('${escapedHsCode}', '${escapedNameKor}', '${escapedNameEng}')">
                    <div class="result-code"><b>${formatHSCode(item.hsCode)}</b></div>
                    <div class="result-name">${escapedNameKor}</div>
                    <div class="result-eng">${escapedNameEng}</div>
                </div>
            `;
        });

        // 북마크 상태 표시 업데이트
        await refreshBookmarkIcons();
    } catch (error) {
        alert(error.message);
        console.error("History 로드 실패:", error);
    }
}

// History 초기화
async function clearHistory() {
    if (!confirm("모든 검색 기록을 삭제하시겠습니까?")) {
        return;
    }

    try {
        const res = await fetch("/api/history", {
            method: "DELETE"
        });
        
        if (!res.ok) {
            const errorText = await res.text();
            console.error("History 초기화 실패:", errorText);
            alert("History 초기화에 실패했습니다.");
            return;
        }

        loadHistory();
    } catch (error) {
        console.error("History 초기화 실패:", error);
        alert("History 초기화 중 오류가 발생했습니다.");
    }
}

// 북마크 토글
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
        await refreshBookmarkIcons();
    } catch (error) {
        alert(error.message);
    }
}

// 북마크 아이콘 상태 업데이트
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

loadHistory();