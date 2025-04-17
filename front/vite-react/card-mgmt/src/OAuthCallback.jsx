import React, { useEffect, useState, useRef } from 'react';
import { useLocation, useNavigate } from 'react-router-dom';
import axios from 'axios';

export default function OAuthCallback() {
  const location = useLocation();
  const navigate = useNavigate();
  const [code, setCode] = useState(null);

  const isCalled = useRef(false); // 중복요청 방지1 

  // ✅ OAuth code 처리 로직
  useEffect(() => {
    if (isCalled.current) return;
    isCalled.current = true;

    const fetchToken = async () => {
      const query = new URLSearchParams(location.search);
      const codeParam = query.get("code");

      if (!codeParam) return; // 중복요청 방지2

      setCode(codeParam); // code 값 셋팅

      try {
        const response = await axios.post(
          `/api/TokenFromAuthCode`,
          { code: codeParam },
          { withCredentials: true }
        );
        if (response.data.status === true) {
          navigate('/');
        } else {
          console.log("토큰 생성 오류");
          navigate('/logout');
        }
      } catch (error) {
        console.error("Error exchanging code for token:", error);
      }
    };

    fetchToken();
  }, [location.search]);

  return (
    <div>
      <h1>OAuth Callback</h1>
      {/*code && <h2>Code: {code}</h2>*/}
      <p>로그인 처리 중입니다...</p>
    </div>
  );
}
