import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import './css/MyPageInfo.css';
import Layout from './layout/Layout';
import axios from 'axios';

export default function MyPageInfo() {
  const navigate = useNavigate();
  const { id } = useParams();

  const [userInfo, setUserInfo] = useState({
    userNo: '', 
    email: '',
    name: '',
    regDate: '',
    loginType: '',
    photoUrl: ''
  });

  useEffect(() => {

    const fetchUserInfo = async () => {
      const API_BASE = import.meta.env.VITE_API_BASE;
  
 
      // 토큰 갱신 성공 후, 사용자 정보 요청
      try {
        const res = await axios.get(`${API_BASE}/MyPageInfo`, { withCredentials: true });
  
        if (res.data.status === true) {
          console.log("내정보 로딩완료");
          console.log(res);
          setUserInfo({
            userNo: res.data.oAuthClient.no || '', 
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


  const handleDeleteAccount = async () => {
    const API_BASE = import.meta.env.VITE_API_BASE;
    if (!window.confirm('정말 탈퇴하시겠습니까?')) {
      return;
    }
    try {
      const res = await axios.post(`${API_BASE}/DeleteAccount`, {
        userNo: userInfo.userNo
      }, { withCredentials: true });
      
      if (res.data.status === true) {
        alert('회원 탈퇴가 완료되었습니다.');
        navigate('/logout');
      } else {
        alert('회원 탈퇴에 실패했습니다.');
      }
    } catch (err) {
      console.error('회원 탈퇴 실패:', err);
      alert('회원 탈퇴 중 오류가 발생했습니다.');
    }
  };

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

                  <div className="profile-info ms-4" style={{ position: 'relative', width: '100%', paddingTop: '40px' }}>
                    <p><strong>생성일:</strong> {userInfo.regDate}</p>
                    <p><strong>로그인방식:</strong> {userInfo.loginType}</p>

                    {/* 회원 탈퇴 버튼 */}
                    <div style={{ marginTop: '40px', textAlign: 'right' }}>
                      <button
                        type="button"
                        className="btn"
                        style={{
                          minWidth: '100px',
                          padding: '6px 12px',
                          backgroundColor: 'transparent',
                          border: '2px solid #dc3545',
                          color: '#dc3545',
                          borderRadius: '6px',
                          fontSize: '14px',
                          fontWeight: 'bold',
                          transition: 'all 0.3s ease',
                        }}
                        onMouseEnter={(e) => {
                          e.target.style.backgroundColor = '#dc3545';
                          e.target.style.color = 'white';
                        }}
                        onMouseLeave={(e) => {
                          e.target.style.backgroundColor = 'transparent';
                          e.target.style.color = '#dc3545';
                        }}
                        onClick={handleDeleteAccount}
                      >
                        회원 탈퇴
                      </button>
                    </div>
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
