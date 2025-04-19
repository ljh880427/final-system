import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import './css/MyPageInfo.css';
import Layout from './layout/Layout';
import axios from 'axios';

export default function MyPageInfo() {
  const navigate = useNavigate();
  const { id } = useParams();

  const [userInfo, setUserInfo] = useState({
    email: '',
    name: '',
    regDate: '',
    loginType: '',
    photoUrl: ''
  });

  useEffect(() => {

    const fetchUserInfo = async () => {
      const API_BASE = import.meta.env.VITE_API_BASE;
  
      // 토큰 갱신
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
        navigate('/signIn'); // 실패 시 로그인 페이지 이동
        return;
      }
  
      // 토큰 갱신 성공 후, 사용자 정보 요청
      try {
        const res = await axios.get(`${API_BASE}/MyPageInfo`, { withCredentials: true });
  
        if (res.data.status === true) {
          console.log("내정보 로딩완료");
          console.log(res);
          setUserInfo({
            email: res.data.email || '',
            name: res.data.oAuthClient.name || '',
            regDate: res.data.oAuthClient.parsRegDate || '',
            loginType: res.data.oAuthClient.issuer || '',
            photoUrl: res.data.PhotoNo || 'https://static.nid.naver.com/images/web/user/default.png?type=s160'
          });
        } else {
          if (res.data.status === "logout") {
            navigate("/logout");
          }
          console.log("내정보 로딩 오류");
          alert("내정보 로딩 오류");
        }
      } catch (err) {
        console.error("사용자 정보 로딩 실패:", err);
        alert("사용자 정보를 불러오는 데 실패했습니다.");
      }
    };
  
    fetchUserInfo();
    
  }, [id]);

  return (
    <div className="mypage-info">
      <Layout afterlogin={true}>
        <div className="container">
          <div className="card" style={{ marginTop:'45px' }}>
            <div className="card-body">
              <h1 style={{ textAlign:'center' }}>내 정보</h1>
              <form>
                <div className="form-group mb-4 d-flex align-items-center">
                  <div className="profile-img-container">
                    <img
                      src={userInfo.photoUrl || 'https://static.nid.naver.com/images/web/user/default.png?type=s160'}
                      alt="Profile Picture"
                      className="profile-img"
                    />
                  </div>

                  <div className="profile-info ms-4">
                    <p><strong>생성일:</strong> {userInfo.regDate}</p>
                    <p><strong>로그인방식:</strong> {userInfo.loginType}</p>
                  </div>
                </div>

                <div className="form-group mb-4">
                  <label htmlFor="email" className="form-label">이메일</label>
                  <input
                    type="email"
                    id="email"
                    name="email"
                    className="form-control"
                    placeholder="이메일을 입력하세요"
                    style={{ textAlign:'center' }}
                    value={userInfo.email}
                    readOnly
                  />
                </div>

                <div className="form-group mb-4">
                  <label htmlFor="name" className="form-label">이름</label>
                  <input
                    type="text"
                    id="name"
                    name="name"
                    className="form-control"
                    placeholder="이름을 입력하세요"
                    style={{ textAlign:'center' }}
                    value={userInfo.name}
                    readOnly
                  />
                </div>

                <br />
                <div className="d-flex flex-column gap-2">
                  <button
                    type="button"
                    className="btn btn-success flex-fill"
                    onClick={() => navigate(`/MyPageEdit/${id}`)}
                  >
                    수정
                  </button>
                  <button
                    type="button"
                    className="btn btn-danger flex-fill"
                    onClick={() => navigate(`/`)}
                  >
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
