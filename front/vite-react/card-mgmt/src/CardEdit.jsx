// CardEdit.jsx
import React, { useState, useRef, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import axios from 'axios';
import Layout from './layout/Layout';
import './css/CardEdit.css';
import cardImgDefault from './img/card.png';

export default function CardEdit() {
  const API_BASE = import.meta.env.VITE_API_BASE;

  const { cardNo } = useParams();
  const [cardNumber, setCardNumber] = useState(cardNo);

  const navigate = useNavigate();
  const [form, setForm] = useState({
    name: '',
    company: '',
    position: '',
    adr: '',
    email: '',
    tel: '',
    fax: '',
    memo: '',
  });
  const [profilePreview, setProfilePreview] = useState('https://static.nid.naver.com/images/web/user/default.png?type=s160');
  const [cardPreview, setCardPreview] = useState(cardImgDefault);
  const [profileFile, setProfileFile] = useState(null);
  const [cardFile, setCardFile] = useState(null);
  const profileInputRef = useRef(null);
  const cardInputRef = useRef(null);

  useEffect(() => {

    // access token refresh 요청
    const refreshToken = async () => {
      try {
        const refresh_status = await axios.get(`${API_BASE}/RefreshToken`, { withCredentials: true });
        if (refresh_status.data.status === true) {
          console.log("token refresh complete");
        } else {
          console.log("token refresh fail!");
        }
      } catch (error) {
        console.error("token refresh error:", error);
      }
    };   
    refreshToken(); // 토큰 리프레시 먼저 실행
    
    axios.get(`${API_BASE}/card/edit?cardNo=${cardNo}`, { withCredentials: true })
      .then((res) => {
        console.log(res.data.cardInfo); // API 응답 구조 확인
        const data = res.data.cardInfo;
        setForm({ // 널처리
          name: data.name || '',
          company: data.company || '',
          position: data.position || '',
          adr: data.adr || '',
          email: data.email || '',
          tel: data.tel || '',
          fax: data.fax || '',
          memo: data.memo || '',
        });
        // 프로필 및 명함 이미지 미리보기 설정
        if (res.data.cardInfo.fileUserNo) {
          setProfilePreview(`${res.data.cardPictureUri}${res.data.cardInfo.fileUserNo}`);
        }
        if (res.data.cardInfo.filePictureNo) {
          setCardPreview(`${res.data.cardPictureUri}${res.data.cardInfo.filePictureNo}`);
        }
      })
      .catch((error) => {
        console.error('데이터를 가져오는 중 에러 발생:', error);
      });
  }, [cardNo]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();

    const formData = new FormData();

    // 폼 데이터 추가
    Object.keys(form).forEach((key) => {
      formData.append(key, form[key]);
    });

    // 파일 데이터 추가
    if (profileFile) {
      formData.append('userPictureFile', profileFile);
    }
    if (cardFile) {
      formData.append('cardPictureFile', cardFile);
    }
    if (cardNumber) {
      formData.append('cardNo', cardNumber);
    }


    axios.post(`${API_BASE}/EditDetailUpdateCardInfo`, formData, { withCredentials: true })
      .then((res) => {

        if (res.data.status === true) {
          alert('수정이 완료되었습니다.');
          navigate(-1); // 이전 페이지로 이동
        } else {
          alert('수정 오류');
        }        
        
      })
      .catch((error) => {
        console.error('수정 중 에러 발생:', error);
      });
  };

  const handleImageChange = (e, type) => {
    const file = e.target.files[0];

    if (file) {
      if (type === 'profile') {
        setProfileFile(file);
        setProfilePreview(URL.createObjectURL(file));
      } else if (type === 'card') {
        setCardFile(file);
        setCardPreview(URL.createObjectURL(file));
      }
    }
  };

  return (
    <Layout afterlogin={true}>
        <div className="card-edit-page container">
            <div className="container">
                <div className="card">
                    <form onSubmit={handleSubmit}>

                    <input type="hidden" name="cardNo" value={cardNumber} />

                    <h3 className="card-title">명함 수정</h3>
                    <div className="profile-section">
                        <img
                        src={profilePreview}
                        alt="profile"
                        className="profile-img"
                        onClick={() => profileInputRef.current.click()}
                        style={{ cursor: 'pointer' }}
                        />
                        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginLeft: 'auto' }}>
                        <img
                            src={cardPreview}
                            alt="card"
                            className="cardreg-img"
                            style={{ width: '190px', cursor: 'pointer' }}
                            onClick={() => cardInputRef.current.click()}
                        />
                        </div>
                        <input
                        type="file"
                        ref={profileInputRef}
                        accept="image/*"
                        onChange={(e) => handleImageChange(e, 'profile')}
                        style={{ display: 'none' }}
                        />
                        <input
                        type="file"
                        ref={cardInputRef}
                        accept="image/*"
                        onChange={(e) => handleImageChange(e, 'card')}
                        style={{ display: 'none' }}
                        />
                    </div>

                    <div className="row">
                        <div className="col-12 col-md-6">
                        <label className="form-label">회사명</label>
                        <input className="form-control" name="company" value={form.company} onChange={handleChange} />
                        </div>
                        <div className="col-12 col-md-6">
                        <label className="form-label">포지션</label>
                        <input className="form-control" name="position" value={form.position} onChange={handleChange} />
                        </div>
                    </div>

                    <div className="row">
                        <div className="col-12 col-md-6">
                        <label className="form-label">이름</label>
                        <input className="form-control" name="name" value={form.name} onChange={handleChange} required />
                        </div>
                        <div className="col-12 col-md-6">
                        <label className="form-label">주소</label>
                        <input className="form-control" name="adr" value={form.adr} onChange={handleChange} />
                        </div>
                    </div>

                    <div className="row">
                        <div className="col-12 col-md-6">
                        <label className="form-label">메일</label>
                        <input className="form-control" name="email" value={form.email} onChange={handleChange} />
                        </div>
                        <div className="col-12 col-md-6">
                        <label className="form-label">전화</label>
                        <input className="form-control" name="tel" value={form.tel} onChange={handleChange} required />
                        </div>
                    </div>

                    <div className="row">
                        <div className="col-12 col-md-6">
                        <label className="form-label">팩스</label>
                        <input className="form-control" name="fax" value={form.fax} onChange={handleChange} />
                        </div>
                        <div className="col-12 col-md-6">
                        <label className="form-label">메모</label>
                        <input className="form-control" name="memo" value={form.memo} onChange={handleChange} />
                        </div>
                    </div>

                    <div className="button-group">
                        <button type="submit" className="btn btn-primary">완료</button>
                        <button type="button" className="btn btn-danger" onClick={() => navigate(-1)}>취소</button>
                    </div>
                    </form>
                </div>
            </div>
        </div>
    </Layout>
  );
}