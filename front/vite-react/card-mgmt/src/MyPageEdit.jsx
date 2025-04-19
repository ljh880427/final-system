import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './css/MyPageEdit.css';
import Layout from './layout/Layout';
import axios from 'axios';

export default function MyPageEdit() {
  const navigate = useNavigate();
  const [form, setForm] = useState({
    no: '',
    name: '',
    email: '',
    pwd: '',
    confirmPwd: '',
    photo: 'https://static.nid.naver.com/images/web/user/default.png?type=s160',
  });

  const [photoFile, setPhotoFile] = useState(null); // 실제 파일 전송용

  const API_BASE = import.meta.env.VITE_API_BASE;

  useEffect(() => {
    const fetchUserInfo = async () => {
      try {
        const refresh_status = await axios.get(`${API_BASE}/RefreshToken`, { withCredentials: true });
  
        if (refresh_status.data.status === true) {
          console.log("token refresh complete");
        } else {
          console.log("token refresh fail!");
          navigate('/signIn');
          return;
        }
      } catch (error) {
        console.error("token refresh error:", error);
        navigate('/signIn');
        return;
      }
  
      // 토큰 갱신 성공 후 사용자 정보 요청
      try {
        const res = await axios.get(`${API_BASE}/MyPageEdit`, { withCredentials: true });
        const data = res.data;
  
        console.log(res);
        if (data.status === 'logout') {
          navigate('/logout');
          return;
        }
  
        setForm((prev) => ({
          ...prev,
          no: data.oAuthClient?.no || '',
          name: data.oAuthClient?.name || '',
          email: data.email || '',
          photo: data.PhotoNo || prev.photo
        }));
      } catch (err) {
        console.error('사용자 정보 로딩 실패:', err);
        alert('사용자 정보 로딩 오류');
      }
    };
  
    fetchUserInfo();
  }, [navigate]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleImageChange = (e) => {
    const file = e.target.files[0];
    if (file) {
      setPhotoFile(file); // 서버로 보낼 파일 세팅

      const reader = new FileReader();
      reader.onload = (event) => {
        setForm((prev) => ({ ...prev, photo: event.target.result }));
      };
      reader.readAsDataURL(file);
    }
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
  
    if (form.pwd !== form.confirmPwd) {
      alert('비밀번호가 일치하지 않습니다.');
      return;
    }
  
    const submitData = new FormData();
    if (photoFile) {
      submitData.append("file", photoFile);
    }
  
    submitData.append("email", form.email);
    submitData.append("name", form.name);
    submitData.append("pwd", form.pwd);
  
    try {
      const res = await axios.post(`${API_BASE}/UserInfoUpdate`, submitData, {
        withCredentials: true,
        headers: {
          "Content-Type": "multipart/form-data"
        }
      });
  
      const result = res.data;
  
      if (result.status === true) {
        alert("정보가 성공적으로 수정되었습니다.");
        navigate(`/MyPageInfo/${form.no}`);
      } else if (result.status === 'logout') {
        alert("세션이 만료되었습니다. 다시 로그인해주세요.");
        navigate(`/logout`);
      } else {
        alert("정보 수정 중 오류가 발생했습니다.");
      }
  
    } catch (err) {
      console.error("수정 요청 실패:", err);
      alert("서버 오류가 발생했습니다.");
    }
  };
  

  return (
    <div className="mypage-edit">
      <Layout afterlogin={true}>
        <div className="container">
          <div className="card">
            <div className="card-body">
              <h1>내 정보 수정</h1>
              <form onSubmit={handleSubmit}>
                <div className="form-group text-center">
                  <img
                    src={form.photo}
                    alt="Profile"
                    className="profile-img"
                    onClick={() => document.getElementById('file-input').click()}
                    style={{ cursor: 'pointer' }}
                  />
                  <input
                    type="file"
                    id="file-input"
                    style={{ display: 'none' }}
                    accept="image/*"
                    onChange={handleImageChange}
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="name" className="form-label">이름</label>
                  <input
                    type="text"
                    id="name"
                    name="name"
                    className="form-control"
                    value={form.name}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="email" className="form-label">이메일</label>
                  <input
                    type="email"
                    id="email"
                    name="email"
                    className="form-control"
                    value={form.email}
                    readOnly
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="pwd" className="form-label">새 비밀번호</label>
                  <input
                    type="password"
                    id="pwd"
                    name="pwd"
                    className="form-control"
                    placeholder="새 비밀번호를 입력하세요"
                    value={form.pwd}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="form-group">
                  <label htmlFor="confirmPwd" className="form-label">비밀번호 확인</label>
                  <input
                    type="password"
                    id="confirmPwd"
                    name="confirmPwd"
                    className="form-control"
                    placeholder="비밀번호를 다시 입력하세요"
                    value={form.confirmPwd}
                    onChange={handleChange}
                    required
                  />
                </div>

                <div className="d-grid gap-2">
                  <button type="submit" className="btn btn-success">수정 완료</button>
                  <button type="button" className="btn btn-danger" onClick={() => navigate('/MyPageInfo/${form.no}')}>
                    취소
                  </button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </Layout>
    </div>
  );
}
