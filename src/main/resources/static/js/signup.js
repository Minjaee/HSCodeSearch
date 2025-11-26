function goBack() {
    window.location.href = "/login";
}

function goMain() {
    window.location.href = "/";
}

async function doSignUp() {
    const username = document.querySelector("#username").value.trim();
    const email = document.querySelector("#email").value.trim();
    const password = document.querySelector("#password").value;
    const confirm = document.querySelector("#confirm").value;

    // 이메일 형식 검사
    const emailRegex = /^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}$/;
    if (!emailRegex.test(email)) {
        alert("올바른 이메일 형식이 아닙니다.");
        return;
    }

    // 비밀번호 규칙 검사
    const pwRegex = /^(?=.*[A-Za-z])(?=.*\d)(?=.*[@$!%*#?&])[A-Za-z\d@$!%*#?&]{8,}$/;
    if (!pwRegex.test(password)) {
        alert("비밀번호는 8자 이상, 영어/숫자/특수문자(@$!%*#?&)를 모두 포함해야 합니다.");
        return;
    }

    if (password !== confirm) {
        alert("비밀번호가 일치하지 않습니다.");
        return;
    }

    // 서버로 전송
    const response = await fetch("/signup", {
        method: "POST",
        headers: {"Content-Type": "application/x-www-form-urlencoded"},
        body: new URLSearchParams({
            username,
            email,
            password
        })
    });

    const result = await response.text();

    if (result === "success") {
        alert("회원가입 완료");
        window.location.href = "/login";
    } else {
        alert(result);
    }
}
