function openMenu() {
    const menu = document.getElementById("sideMenu");
    const hamburger = document.querySelector(".hamburger");

    hamburger.style.display = "none";     // 햄버거 숨김
    menu.style.display = "block";

    setTimeout(() => {
        menu.style.right = "0";           // 슬라이드 등장
    }, 10);

    // ★ 로그인 상태 반영
    loadMenuUserInfo();
}


function closeMenu() {
    const menu = document.getElementById("sideMenu");
    const hamburger = document.querySelector(".hamburger");

    menu.style.right = "-380px";          // 오른쪽 밖으로
    setTimeout(() => {
        menu.style.display = "none";      // 메뉴 숨김
        hamburger.style.display = "block"; // 햄버거 복구
    }, 350);                              // transition 0.35s 끝난 뒤
}

function goLogin() {
    window.location.href = "/login";
}

// 전역 함수로 노출 (HTML에서 호출 가능하도록)
window.goLogin = goLogin;
window.goBookmark = goBookmark;
window.goHistory = goHistory;

function goMain() {
    window.location.href = "/";
}

async function loadMenuUserInfo() {
    const res = await fetch("/api/user");
    const user = await res.json();

    const usernameEl = document.querySelector(".login-section h2");
    const emailEl = document.querySelector(".login-section p");
    const btnEl = document.querySelector(".login-section .login-btn");
    const bookmarkEl = document.getElementById("bookmark-area");
    const menu = document.getElementById("sideMenu");

    if (!user) {
        // 로그인 안 됨
        usernameEl.textContent = "Log in";
        emailEl.innerHTML = "To save your bookmarks,<br>passwords, and cards";
        btnEl.textContent = "Log in";
        btnEl.onclick = goLogin;
        bookmarkEl.style.display = "none";   // ★ 숨김
        menu.classList.remove("large");
    } else {
        // 로그인 됨
        usernameEl.textContent = user.username;
        emailEl.textContent = user.email;
        btnEl.textContent = "Log out";
        btnEl.onclick = doLogout;
        bookmarkEl.style.display = "block";  // ★ 표시
        menu.classList.add("large");
    }
}


async function doLogout() {
    await fetch("/logout", { method: "POST" });
    alert("로그아웃 되었습니다.");
    window.location.reload();
}

function goBookmark() {
    window.location.href = "/bookmark";
}

function goHistory() {
    window.location.href = "/history";
}
