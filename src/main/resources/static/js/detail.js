function goMain() {
    window.location.href = "/";
}

function goBack() {
    history.back();
}

// URL 파라미터에서 hsCode 추출
const params = new URLSearchParams(window.location.search);
const hsCode = params.get("code");

async function loadDetail() {

    const res = await fetch(`/api/detail?code=${encodeURIComponent(hsCode)}`);
    const data = await res.json();

    const box = document.getElementById("detail-content");

    if (!data) {
        box.innerHTML = "<p>상세 정보를 찾을 수 없습니다.</p>";
        return;
    }

    box.innerHTML = `
        <h2 class="hs-title">${format(data.hsCode)}</h2>

        <div class="hs-names">
            <p><b>국문명:</b> ${data.nameKor ?? "정보 없음"}</p>
            <p><b>영문명:</b> ${data.nameEng ?? "정보 없음"}</p>
        </div>

        <div class="hs-desc">
            <p><b>설명:</b></p>
            <p>${data.description ?? "설명 없음"}</p>
        </div>
    `;
}

loadDetail();

function format(code) {
    if (!code) return "";

    const clean = code.replace(/\D/g, ""); // 숫자만 추출

    // 10자리(HS code full) 기준으로 포맷팅
    // 1234.56.7890 형태
    if (clean.length === 10) {
        return `${clean.slice(0,4)}.${clean.slice(4,6)}.${clean.slice(6,10)}`;
    }

    // 6자리 코드면 1234.56 형태
    if (clean.length === 6) {
        return `${clean.slice(0,4)}.${clean.slice(4,6)}`;
    }

    // 4자리면 1234
    if (clean.length === 4) {
        return clean.slice(0,4);
    }

    return clean;
}