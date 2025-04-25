// components/PrivateRoute.jsx
import React, { useEffect, useState } from 'react';
import { Navigate } from 'react-router-dom';
import axios from 'axios';

export default function PrivateRoute({ children }) {
  const [authChecked, setAuthChecked] = useState(false);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const API_BASE = import.meta.env.VITE_API_BASE;

  useEffect(() => {
    const checkAuth = async () => {
      try {
        const response = await axios.get(`${API_BASE}/RefreshToken`, {
          withCredentials: true,
        });

        if (response.data.status === true) {
          console.log('토큰 유효함');
          setIsAuthenticated(true);
        } else {
          console.warn('토큰 유효하지 않음');
          setIsAuthenticated(false);
        }
      } catch (error) {
        console.error('토큰 체크 중 오류:', error);
        setIsAuthenticated(false);
      } finally {
        setAuthChecked(true);
      }
    };

    checkAuth();
  }, []);

  if (!authChecked) return <div>인증 확인 중...</div>;

  return isAuthenticated ? children : <Navigate to="/signIn" />;
}
