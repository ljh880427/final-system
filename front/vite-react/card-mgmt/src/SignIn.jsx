// SignIn.jsx
import React, { useState, useEffect } from 'react';
import facebook from './img/facebook.png';
import naver from './img/naver.png';
import kakao from './img/kakao.png';
import google from './img/google.png';
import Layout from './layout/Layout';
import axios from 'axios';
import { useNavigate, useLocation } from 'react-router-dom';
import './css/signIn.css';

export default function SignIn() {
  const [email, setEmail] = useState('');
  const [pwd, setPwd] = useState('');
  const navigate = useNavigate();
  const location = useLocation();
  const code = location.state?.code || null;

  const API_BASE = import.meta.env.VITE_API_BASE;

  // ✅ SignIn 페이지 진입 시 로그아웃 처리
  useEffect(() => {
    const logoutUser = async () => {
      if (location.pathname === '/signIn') {
        try {
          const res = await axios.get(`${API_BASE}/oauth2/logout`, {
            withCredentials: true
          });
          console.log('로그아웃 요청 결과:', res);
        } catch (err) {
          console.error('로그아웃 중 오류 발생:', err);
        }
      }
    };

    logoutUser();
  }, [location.pathname, API_BASE]);

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      if (code !== null) {
        navigate('/'); // ✅ 로그인 및 코드 GET 성공 시 홈으로 이동
      } else {
        const response = await axios.post(`${API_BASE}/signIn`, { email, pwd }, { withCredentials: true });
        console.log('로그인 응답:', response);
        if (response.data.status === true) {
          console.log('로그인 성공');
          const { data: redirectUrl } = await axios.post(`${API_BASE}/getCodeUrl`, { email }, { withCredentials: true });
          window.location.href = redirectUrl;
        } else {
          alert('로그인 실패');
        }
      }
    } catch (error) {
      console.error('로그인 에러:', error);
      alert('서버 오류가 발생했습니다.');
    }
  };

  return (
    <Layout prelogin={true}>
      <div className="container" style={{ marginTop: '40px' }}>
        <div className="container text-center">
          <form className="form-signin" onSubmit={handleSubmit}>
            <h2 className="form-signin-heading">Sign in</h2>
            <small className="text-muted" style={{ fontSize: '11px' }}>
              Connect Cafe & Blog with your favorite social network
            </small>
            <br /><br />

            <p>
              <a className="social-btn" href="/oauth2/login/facebook" style={{ padding: '1px 6px' }}>
                <img src={facebook} alt="Facebook" style={{ width: '55px', height: '50px' }} />
              </a>
              <a className="social-btn" href="/oauth2/login/naver" style={{ padding: '1px 6px' }}>
                <img src={naver} alt="Naver" style={{ width: '55px', height: '50px' }} />
              </a>
            </p>
            <p>
              <a className="social-btn" href="/oauth2/login/kakao" style={{ padding: '1px 6px' }}>
                <img src={kakao} alt="KaKao" style={{ width: '55px', height: '50px' }} />
              </a>
              <a className="social-btn" href="/oauth2/login/google" style={{ padding: '1px 6px' }}>
                <img src={google} alt="Google" style={{ width: '55px', height: '50px' }} />
              </a>
            </p>

            <small className="text-muted" style={{ fontSize: '11px' }}>
              Or sign in with cafe & blog
            </small>
            <br /><br />

            <input
              className="form-control login-input"
              type="email"
              name="email"
              id="email"
              placeholder="Email Address"
              required
              value={email}
              onChange={(e) => setEmail(e.target.value)}
            />
            <input
              className="form-control login-input-pass"
              name="pwd"
              id="pwd"
              placeholder="Password"
              type="password"
              required
              value={pwd}
              onChange={(e) => setPwd(e.target.value)}
            />

            <button
              className="btn btn-lg btn-primary btn-block btn-center"
              style={{ marginBottom: '10px', marginTop: '5px' }}
              type="submit"
            >
              Sign in
            </button>
            <br />
            <small className="create-account text-muted" style={{ fontSize: '11px', marginBottom: '10px' }}>
              Don't have a cafe & blog or social network account?
            </small>
            <a href="/signUp">
              <button
                className="btn btn-sm btn-outline-secondary"
                type="button"
                style={{ width: '70px', height: '30px', fontSize: '11px' }}
              >
                Sign Up
              </button>
            </a>
          </form>
        </div>
      </div>
    </Layout>
  );
}
