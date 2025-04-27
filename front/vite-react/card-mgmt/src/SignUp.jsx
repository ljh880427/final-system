// SignUp.jsx
import React, { useState } from 'react';
import axios from 'axios';
import facebook from './img/facebook.png';
import naver from './img/naver.png';
import kakao from './img/kakao.png';
import google from './img/google.png';
import Layout from './layout/Layout';
import './css/signIn.css';
import { useNavigate } from 'react-router-dom';

export default function SignUp() {
  const [email, setEmail] = useState('');
  const [name, setName] = useState('');
  const [pwd, setPwd] = useState('');
  const navigate = useNavigate();

  const handleSubmit = async (e) => {
    e.preventDefault();
    try {
      const API_BASE = import.meta.env.VITE_API_BASE;
      const response = await axios.post(`${API_BASE}/signUp`, { email, name, pwd }, { withCredentials: true });
      console.log(response);
      if (response.data.status === true) {

        if(response.data.account_exist === true){
          alert('가입하려는 이메일이 존재합니다. 다른 이메일로 시도해주세요.');
          return;
        }
        
        alert('회원가입이 완료되었습니다. 로그인 해주세요.');
        navigate('/signIn');
      }
    } catch (error) {
      console.error('회원가입 에러:', error);
      alert('서버 오류가 발생했습니다.');
    }
  };

  return (
    <Layout prelogin={true}>
      <div className="container" style={{ marginTop: '40px' }}>
        <div className="container text-center">
          <form className="form-signin" onSubmit={handleSubmit}>
            <h2 className="form-signin-heading">Sign up</h2>
            <small className="text-muted" style={{ fontSize: '11px' }}>Connect Cafe & Blog with your favorite social network</small>
            <br /><br />

            <p>
              <a className="social-btn" href="/oauth2/login/google" style={{ padding: '1px 6px' }}>
                <img src={google} alt="Google" style={{ width: '61px' }} />
              </a>
              <a className="social-btn" href="/oauth2/login/naver" style={{ padding: '1px 6px' }}>
                <img src={naver} alt="Naver" style={{ width: '55px' }} />
              </a>
            </p>

            <small className="text-muted" style={{ fontSize: '11px' }}>Or sign in with cafe & blog</small>
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
              className="form-control login-input"
              type="text"
              name="name"
              id="name"
              placeholder="Name"
              required
              value={name}
              onChange={(e) => setName(e.target.value)}
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

            <button className="btn btn-lg btn-primary btn-block btn-center" style={{ marginBottom: '10px', marginTop:'5px' }} type="submit">
              Sign up
            </button>
            <br />
            <small className="create-account text-muted" style={{ fontSize: '11px', marginBottom: '10px' }}>Do you have a cafe & blog or social network account?</small>
            <a href="/signIn">
              <button
                className="btn btn-sm btn-outline-secondary"
                type="button"
                style={{ width: '70px', height: '30px', fontSize: '11px' }}
              >
                Sign In
              </button>
            </a>
          </form>
        </div>
      </div>
    </Layout>
  );
}
