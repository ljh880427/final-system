// App.jsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import MainLayout from './MainLayout';
import SignIn from './SignIn';
import SignUp from './SignUp';
import Logout from './Logout';
import CardDetail from './CardDetail';
import CardEdit from './CardEdit';
import MyPageInfo from './MyPageInfo';
import MyPageEdit from './MyPageEdit';
import OAuthCallback from './OAuthCallback';
import PrivateRoute from './PrivateRoute'; // 새로 추가

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<MainLayout />} />
        <Route path="/signIn" element={<SignIn />} />
        <Route path="/signUp" element={<SignUp />} />
        <Route path="/logout" element={<Logout />} />
        <Route path="/callback/custom" element={<OAuthCallback />} />
        
        {/* 보호된 라우트 */}
        <Route path="/detail/:id" element={<PrivateRoute><CardDetail /></PrivateRoute>} />
        <Route path="/edit/:cardNo" element={<PrivateRoute><CardEdit /></PrivateRoute>} />
        <Route path="/MyPageInfo/:id" element={<PrivateRoute><MyPageInfo /></PrivateRoute>} />
        <Route path="/MyPageEdit/:id" element={<PrivateRoute><MyPageEdit /></PrivateRoute>} />
      </Routes>
    </Router>
  );
}
