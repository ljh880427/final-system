import React, { useState, useEffect } from 'react';
import logo from '../img/logo_oneteam.png';
import '../css/main.css';
import '../css/header.css';
import 'bootstrap/dist/css/bootstrap.min.css';

export const Header = ({ userNo, prelogin = false, afterlogin = false  }) => {
  const [isMenuOpen, setIsMenuOpen] = useState(false);

  const toggleMenu = () => {
    setIsMenuOpen(prev => !prev);
  };

  useEffect(() => {
    const handleResize = () => {
      if (window.innerWidth >= 576) {
        setIsMenuOpen(false);
      }
    };
    window.addEventListener('resize', handleResize);
    return () => window.removeEventListener('resize', handleResize);
  }, []);

  return (
    <nav className="navbar navbar-expand-sm navbar-light bg-light border-bottom px-3">
      <div className="container-fluid d-flex justify-content-between align-items-center">
        <a className="navbar-brand p-0" href="/">
          <img src={logo} alt="oneteam logo" width="140" height="45" className="ms-2" />
        </a>

        <button
          className="navbar-toggler d-block d-sm-none ms-auto"
          type="button"
          onClick={toggleMenu}
          aria-controls="navbarNav"
          aria-expanded={isMenuOpen}
          aria-label="Toggle navigation"
        >
          <span className="navbar-toggler-icon"></span>
        </button>

        <div className={`collapse navbar-collapse side-slide-menu ${isMenuOpen ? 'show' : ''}`} id="navbarNav">
          <button className="side-toggle navbar-toggler d-block d-sm-none" onClick={toggleMenu} aria-label="닫기 버튼">
            <span className="navbar-toggler-icon"></span>
          </button>

          <ul className="navbar-nav ms-auto mb-2 mb-lg-0">
          {prelogin && (
            <>
              <li className="nav-item"><a className="nav-link" href="/signIn">로그인</a></li>
              <li className="nav-item"><a className="nav-link" href="/signUp">회원가입</a></li>
            </>
          )}

          {afterlogin && (
            <>
              <li className="nav-item">
              <a className="nav-link" href={userNo ? `/MyPageInfo/${userNo}` : "#"}>내정보</a>
              </li>
              <li className="nav-item"><a className="nav-link" href="/logout">로그아웃</a></li>
            </>
          )}
            
            
          </ul>
        </div>
      </div>
    </nav>
  );
};

export const Footer = () => (
  <footer className="app-footer text-center py-2 bg-light mt-4">
    <p>&copy; 2025 Business Card System. All rights reserved.</p>
  </footer>
);

export default function Layout({ children, userNo, prelogin = false, afterlogin = false }) {
  return (
    <div className="layout-wrapper">
      <Header userNo={userNo} prelogin={prelogin} afterlogin={afterlogin} />
      <main>{children}</main>
      <Footer />
    </div>
  );
}
