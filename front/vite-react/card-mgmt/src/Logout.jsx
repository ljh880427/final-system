// Logout.jsx
import { useEffect } from 'react';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

export default function Logout() {
  const navigate = useNavigate();

  const API_BASE = import.meta.env.VITE_API_BASE;

  useEffect(() => {
    const doLogout = async () => {
      try {
        const res = await axios.get(`${API_BASE}/oauth2/logout`, {
          withCredentials: true
        });
        console.log(res);
        if (res.data.status === true) {
          navigate('/signIn'); // 로그아웃 성공 시 로그인 페이지로
        } else {
          alert('로그아웃 처리 실패');
        }
      } catch (error) {
        console.error('로그아웃 중 오류 발생:', error);
        alert('서버 오류로 로그아웃 실패');
      }
    };

    doLogout();
  }, [navigate]);

  return null; // 화면에는 아무것도 표시되지 않음
}
