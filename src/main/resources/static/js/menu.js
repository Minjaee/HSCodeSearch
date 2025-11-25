function openMenu() {
    const menu = document.getElementById("sideMenu");
    const hamburger = document.querySelector(".hamburger");

    hamburger.style.display = "none";     // 햄버거 숨김
    menu.style.display = "block";

    setTimeout(() => {
        menu.style.right = "0";           // 슬라이드 등장
    }, 10);
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

function goMain() {
    window.location.href = "/";
}