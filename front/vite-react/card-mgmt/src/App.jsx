// App.jsx
import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import MainLayout from './MainLayout'; // 기존 App 내용을 이 파일로 이동
import SignIn from './SignIn';         // 로그인 페이지
import SignUp from './SignUp';         // 회원가입 페이지
import Logout from './Logout';
import CardDetail from './CardDetail';  // 카드 디테일 정보(상세)
import CardEdit from './CardEdit'; // 카드 수정 컴포넌트 추가
import MyPageInfo from './MyPageInfo'; // 카드 수정 컴포넌트 추가
import MyPageEdit from './MyPageEdit'; // 카드 수정 컴포넌트 추가

export default function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<MainLayout />} />
        <Route path="/signIn" element={<SignIn />} />
        <Route path="/signUp" element={<SignUp />} />
        <Route path="/logout" element={<Logout />} />
        <Route path="/detail/:id" element={<CardDetail />} />
        <Route path="/edit/:cardNo" element={<CardEdit />} /> 
        <Route path="/MyPageInfo/:id" element={<MyPageInfo />} /> 
        <Route path="/MyPageEdit/:id" element={<MyPageEdit />} /> 
      </Routes>
    </Router>
  );
}
