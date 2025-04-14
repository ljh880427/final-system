// 프로필 이미지 변경 함수
function updateProfileImage(event) {
    const file = event.target.files[0];
    const reader = new FileReader();

    reader.onload = function(e) {
        // 파일이 로드된 후, 이미지 src를 변경
        document.getElementsByClassName('profile-img')[0].src = e.target.result;
    }

    // 파일 읽기
    if (file) {
        reader.readAsDataURL(file);
    }
}

document.getElementById('MyPageEditForm').addEventListener('submit', function(event) {
    event.preventDefault(); // Prevent default form submission

    var password = document.getElementById("pwd").value;
    var confirmPassword = document.getElementById("confirm-pwd").value;

    if (password !== confirmPassword) {
        alert("비밀번호가 일치하지 않습니다. 다시 확인해주세요.");
        return;
    }

    // Get form data
    const formData = new FormData();
    formData.append("file", document.getElementById("file-input").files[0]); // 파일 객체
    formData.append("email", document.getElementById("email").value);
    formData.append("name", document.getElementById("name").value);
    formData.append("pwd", document.getElementById("pwd").value);

    // Send data using Axios
    axios
      .post("/UserInfoUpdate", formData, {
        withCredentials: true,
        headers: {
          "Content-Type": "multipart/form-data", // 중요!
        },
      })
      .then((res) => {
        console.log(res);
        if (res.data === true) {
          document.location.href = "/MyPageInfo"; // Redirect to home
        }
      })
      .catch((err) => {
        console.log(err);
      });
});