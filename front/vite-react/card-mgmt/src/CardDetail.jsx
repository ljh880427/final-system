import React, { useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import Layout from './layout/Layout';
import './css/CardDetail.css';
import defaultCard from './img/card.png';
import defaultProfile from './img/default.png';
import axios from 'axios';


export default function CardDetail() {
  const navigate = useNavigate();

  const API_BASE = import.meta.env.VITE_API_BASE;

  const [user, setUser] = useState({ no: ''});
  const { id } = useParams(); // URL의 cardNo
  const [cardData, setCardData] = useState(null);
  const [cardPictureUri, setCardPictureUri] = useState('');

  const getImageSrc = (baseUri, fileNo, defaultImg) => {
    return fileNo && fileNo !== 0 ? `${baseUri}${fileNo}` : defaultImg;
  };

  useEffect(() => {
    axios
      .get(`${API_BASE}/card/detail?cardNo=${id}`, { withCredentials: true })
      .then((res) => {
        console.log(res);
        if (res.data.status === true) {
          setCardData(res.data.cardInfo);
          setCardPictureUri(res.data.cardPictureUri);
          setUser({
            no: res.data.no || ''
          });
        } else {
          alert('명함 정보를 불러올 수 없습니다.');
          navigate('/');
        }
      })
      .catch((err) => {
        console.error('명함 상세 조회 실패:', err);
      });
  }, [id]);

  if (!cardData) return <div>로딩 중...</div>;

  return (
    <Layout afterlogin={true} userNo={user.no}>
      <div className="card-detail-page">
        <div className="container">
          <div className="card">
            <h3 className="card-title">명함 상세</h3>

            <div className="profile-section">
              <img
                src={getImageSrc(cardPictureUri, cardData.fileUserNo, defaultProfile)}
                className="profile-img"
                alt="profile"
              />
              <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', marginLeft: 'auto' }}>
                <img
                  src={getImageSrc(cardPictureUri, cardData.filePictureNo, defaultCard)}
                  className="cardreg-img"
                  alt="card"
                  style={{ width: '190px' }}
                />
              </div>
            </div>

            {/* 카드 정보 출력 */}
            <div className="row mb-4">
              <div className="col-12 col-md-6">
                <label className="form-label">회사명</label>
                <input className="form-control" value={cardData.company || ''} readOnly />
              </div>
              <div className="col-12 col-md-6">
                <label className="form-label">포지션</label>
                <input className="form-control" value={cardData.position || ''} readOnly />
              </div>
            </div>

            <div className="row mb-4">
              <div className="col-12 col-md-6">
                <label className="form-label">이름</label>
                <input className="form-control" value={cardData.name || ''} readOnly />
              </div>
              <div className="col-12 col-md-6">
                <label className="form-label">주소</label>
                <input className="form-control" value={cardData.adr || ''} readOnly />
              </div>
            </div>

            <div className="row mb-4">
              <div className="col-12 col-md-6">
                <label className="form-label">메일</label>
                <input className="form-control" value={cardData.email || ''} readOnly />
              </div>
              <div className="col-12 col-md-6">
                <label className="form-label">전화</label>
                <input className="form-control" value={cardData.tel || ''} readOnly />
              </div>
            </div>

            <div className="row mb-4">
              <div className="col-12 col-md-6">
                <label className="form-label">팩스</label>
                <input className="form-control" value={cardData.fax || ''} readOnly />
              </div>
              <div className="col-12 col-md-6">
                <label className="form-label">메모</label>
                <input className="form-control" value={cardData.memo || ''} readOnly />
              </div>
            </div>

            <div className="button-group">
              <button className="btn btn-primary" onClick={() => navigate(`/edit/${id}`)}>수정</button>
              <button className="btn btn-danger" onClick={() => navigate('/')}>취소</button>
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}
