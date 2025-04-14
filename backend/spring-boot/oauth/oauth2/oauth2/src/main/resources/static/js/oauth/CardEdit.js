let targetImgId = ""; // 클릭된 img의 id 저장


function EditUserSetTargetImage(imgId) {
    targetImgId = imgId;
    document.getElementById('file-input-profile-editdetail').click(); // 파일 선택 창 열기
}

function EditUserProfileupdateImage(event) {
    const file = event.target.files[0];
    const reader = new FileReader();

    reader.onload = function(e) {
        if (targetImgId) {
            document.getElementById(targetImgId).src = e.target.result; // 클릭된 img 요소 변경
        }
    }

    if (file) {
        reader.readAsDataURL(file);
    }
}

function EditPictureSetTargetImage(imgId) {
    targetImgId = imgId;
    document.getElementById('file-input-card-editdetail').click(); // 파일 선택 창 열기
}

// 명함 사진 등록
function EditCardupdateProfileImage(event) {
    const file = event.target.files[0];
    const reader = new FileReader();

    reader.onload = function(e) {
        if (targetImgId) {
            document.getElementById(targetImgId).src = e.target.result; // 클릭된 img 요소 변경
        }
    }

    if (file) {
        reader.readAsDataURL(file);
    }
}


function CardInfoDetail(element) {
    var cardNo = element.getAttribute("data-card-no");
    window.location.href = '/card/detail?cardNo=' + cardNo;
}


document.getElementById('EditDetailForm').addEventListener('submit', function(event) {
    event.preventDefault();

    // FormData 객체 생성
    const formData = new FormData();
    formData.append("userPictureFile", document.getElementById("file-input-profile-editdetail").files[0]);
    formData.append("cardPictrueFile", document.getElementById("file-input-card-editdetail").files[0]);
    formData.append("cardNo", document.getElementById('cardNo').value);
    formData.append("company", document.getElementById('company').value);
    formData.append("position", document.getElementById('position').value);
    formData.append("name", document.getElementById('name').value);
    formData.append("adr", document.getElementById('adr').value);
    formData.append("email", document.getElementById('email').value);
    formData.append("tel", document.getElementById('tel').value);
    formData.append("fax", document.getElementById('fax').value);
    formData.append("memo", document.getElementById('memo').value);

    // Axios를 사용한 multipart/form-data 전송
    axios.post('/EditDetailUpdateCardInfo', formData, {
        headers: {
            'Content-Type': 'multipart/form-data'
        },
        withCredentials: true
    })
    .then((res) => {
        console.log(res);
        if (res.data.status === true) {
            alert("명함 업데이트 완료.");
            window.location.href = '/card/detail?cardNo=' + document.getElementById('cardNo').value;
        } else {
            alert("명함 업데이트 오류.");
        }
    })
    .catch((err) => {
        console.error(err);
    });
});
