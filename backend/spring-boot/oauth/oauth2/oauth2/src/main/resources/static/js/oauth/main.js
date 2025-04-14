
// 로그아웃 함수
function logout() {
    axios.get("/oauth2/logout", {}, { withCredentials: true })
        .then(res => {

			console.log(res);
            if (res.data) {
                // 로그아웃 성공 후 리디렉션
                document.location.href = "/oauth2/logout";

            }
        })
        .catch(err => {
            console.log(err);
        });
}

function regCard() {
    // "명함 등록" 버튼 클릭 시 바로 "writereg" 탭으로 전환
    switchTab('writereg');
}

function switchTab(tabId) {

    if(tabId === "cardlist") {
        window.location.href = '/'; // "/" 경로로 리디렉트
        return; // 이후 코드 실행 방지
    }
    document.querySelectorAll('.tab-content').forEach(tab => tab.classList.remove('active'));
    document.querySelectorAll('.tab').forEach(tab => tab.classList.remove('active'));
    document.getElementById(tabId).classList.add('active');
    document.querySelector(`.tab[onclick="switchTab('${tabId}')"]`).classList.add('active');
}

// 사진 프로필 등록 변경
function regProfileupdateProfileImage(event) {
    const file = event.target.files[0];
    const reader = new FileReader();

    reader.onload = function(e) {
        // 파일이 로드된 후, 이미지 src를 변경
        document.getElementById("regProfile").src = e.target.result;
    }

    // 파일 읽기
    if (file) {
        reader.readAsDataURL(file);
    }
}

// 명함 사진 등록
function regCardupdateProfileImage(event) {
    const file = event.target.files[0];
    const reader = new FileReader();

    reader.onload = function(e) {
        // 파일이 로드된 후, 이미지 src를 변경
        document.getElementById("regCard").src = e.target.result;
    }

    // 파일 읽기
    if (file) {
        reader.readAsDataURL(file);
    }
}

// 명함 삭제
function deleteCard(button, cardNo) {
    // 버튼을 클릭한 명함 카드를 찾아서 삭제
//    var card = button.closest('.post-card');
//    card.remove();

    // 삭제 전 확인 메시지
    if (!confirm("삭제하시겠습니까?")) {
        return; // 사용자가 취소하면 함수 종료
    }

    const cardNumber = parseInt(cardNo, 10);

    const delCardInfo = {
        cardNo: cardNumber  // 카드 번호 추가
    };

    // Axios를 사용해 JSON 방식으로 데이터 전송
    axios.post('/DelCardInfo', delCardInfo, {
        headers: {
            'Content-Type': 'application/json'
        },
        withCredentials: true
    })
    .then((res) => {
        console.log(res);
        if (res.data.status === true) {
            alert(res.data.msg);
            window.location.href = '/'; // "/" 경로로 리디렉트
        }
        else {
            alert(res.data.msg);
        }
    })
    .catch((err) => {
        console.error(err);
    });
}

// 명함 검색 기능 (GET 요청)
function searchCards() {
    var input = document.getElementById('searchInput').value.trim(); // 공백 제거

    // 검색어를 URL 파라미터로 추가하여 페이지 새로고침
    window.location.href = '/?searchKeyWord=' + encodeURIComponent(input);
}

function CardInfoDetail(element) {
    var cardNo = element.getAttribute("data-card-no");
    window.location.href = '/card/detail?cardNo=' + cardNo;
}

// Axios를 사용한 폼 제출
document.getElementById('regForm').addEventListener('submit', function(event) {
    event.preventDefault(); // 폼 기본 제출을 막기

    // 폼 데이터 가져오기
    const cardInfo = {
        company: document.getElementById('company').value,
        position: document.getElementById('position').value,
        name: document.getElementById('name').value,
        adr: document.getElementById('adr').value,
        email: document.getElementById('email').value,
        tel: document.getElementById('tel').value,
        fax: document.getElementById('fax').value,
        memo: document.getElementById('memo').value
    };

    // Axios를 사용해 JSON 방식으로 데이터 전송
    axios.post('/RegCardInfo', cardInfo, {
        headers: {
            'Content-Type': 'application/json'
        },
        withCredentials: true
    })
    .then((res) => {
        console.log(res);
        if (res.data.status === true) {
            alert("명함 등록 완료.");
            window.location.href = '/'; // "/" 경로로 리디렉트
        }
        else {
            alert("명함 등록 오류.");
        }
    })
    .catch((err) => {
        console.error(err);
    });
});


document.getElementById('PictureRegForm').addEventListener('submit', function(event) {
    event.preventDefault();

    // FormData 객체 생성
    const formData = new FormData();
    formData.append("userPictureFile", document.getElementById("file-input-profile").files[0]);
    formData.append("cardPictrueFile", document.getElementById("file-input-card").files[0]);
    formData.append("name", document.getElementById('picture_card_name').value);
    formData.append("tel", document.getElementById('picture_card_tel').value);

    // Axios를 사용한 multipart/form-data 전송
    axios.post('/PictureRegCardInfo', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        withCredentials: true
    })
    .then((res) => {
        console.log(res);
        if (res.data.status === true) {
            alert("명함 등록 완료.");
            window.location.href = '/';
        } else {
            alert("명함 등록 오류.");
        }
    })
    .catch((err) => {
        console.error(err);
    });
});

