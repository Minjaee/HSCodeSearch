function goBack() {
    window.location.href = "/";
}

async function doLogin() {
    const id = document.querySelector("#login-id").value.trim();
    const password = document.querySelector("#login-password").value;

    if (id === "" || password === "") {
        alert("아이디/이메일과 비밀번호를 입력해주세요.");
        return;
    }

    const response = await fetch("/api/login", {
        method: "POST",
        headers: { "Content-Type": "application/x-www-form-urlencoded" },
        body: new URLSearchParams({
            id,
            password
        })
    });

    const result = await response.text();

    if (result === "success") {
        window.location.href = "/";  // 메인으로 이동
    } else {
        alert(result);
    }
}


function goMain() {
    window.location.href = "/";
}

function goSignUp() {
    window.location.href = "/signup";
}
