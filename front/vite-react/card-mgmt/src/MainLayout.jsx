import React, { useState, useEffect, useRef } from 'react';
import Layout from './layout/Layout';
import './css/main.css';
import defaultProfile from './img/default.png';
import cardDefault from './img/card.png';
import { useNavigate } from 'react-router-dom';
import axios from 'axios';

export default function App() {

  const API_BASE = import.meta.env.VITE_API_BASE;

  const navigate = useNavigate();
  const [activeTab, setActiveTab] = useState('cardlist');
  
  const [user, setUser] = useState({ no: '', name: '', email: '', PhotoNo: '' });
  const [cards, setCards] = useState([]);
  const [cardImageBaseUri, setCardImageBaseUri] = useState('');
  const [searchKeyword, setSearchKeyword] = useState('');
  const [profileImage, setProfileImage] = useState(defaultProfile);
  const [cardImage, setCardImage] = useState(cardDefault);
  const [profileFile, setProfileFile] = useState(null);
  const [cardFile, setCardFile] = useState(null);
  const [formData, setFormData] = useState({
    name: '',
    position: '',
    company: '',
    adr: '',
    tel: '',
    fax: '',
    email: '',
    memo: '',
  });


// 명함 삭제 함수
const handleDelete = async (cardNo, event) => {
  event.preventDefault(); // 기본 동작 방지
  event.stopPropagation(); // 이벤트 전파 방지

  if (!window.confirm("정말루 삭제하시겠습니까?")) {
    return; // 사용자가 취소하면 함수 종료
  }

  try {
    const response = await axios.post(`${API_BASE}/DelCardInfo`, { cardNo }, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    });

    if (response.data.status === true) {
      alert(response.data.msg);
      setCards(prevCards => prevCards.filter(card => card.no !== cardNo)); // 상태에서 해당 카드 제거
    } else {
      alert(response.data.msg);
    }
  } catch (error) {
    console.error("명함 삭제 실패:", error);
    alert("명함 삭제 중 오류가 발생했습니다.");
  }
};

const fileInputRef = useRef();

// 사진으로 등록시 
const handleSubmit = async (e) => {
  e.preventDefault();

  if (!fileInputRef.current.files.length) {
    alert('명함 사진을 등록해주세요.');
    return;
  }

  const data = new FormData();
  if (profileFile) data.append('userPictureFile', profileFile);
  if (cardFile) data.append('cardPictureFile', cardFile);
  data.append('name', formData.name);
  data.append('tel', formData.tel);

  try {
    const response = await axios.post(`${API_BASE}/PictureRegCardInfo`, data, {
      headers: {
        'Content-Type': 'multipart/form-data',
      },
      withCredentials: true,
    });
    if (response.data.status === true) {
      alert('명함 등록 완료.');      
      window.location.href = '/';
    } else {
      alert('명함 등록 오류.');
    }
  } catch (error) {
    console.error('명함 등록 실패:', error);
    alert('명함 등록 중 오류가 발생했습니다.');
  }
};


// 데이터를 가져오는 함수
const fetchData = async (keyword = '') => {
  const apiUrl = keyword
    ? `${API_BASE}?searchKeyWord=${encodeURIComponent(keyword)}`
    : `${API_BASE}`;

  // // access token refresh 요청
  // const refreshToken = async () => {
  //   try {
  //     const refresh_status = await axios.get(`${API_BASE}/RefreshToken`, { withCredentials: true });
  //     if (refresh_status.data.status === true) {
  //       console.log("token refresh complete");
  //       return true;
  //     } else {
  //       console.log("token refresh fail!");
  //       navigate('/signIn');
  //       return false;
  //     }
  //   } catch (error) {
  //     console.error("token refresh error:", error);
  //     navigate('/signIn');
  //     return false;
  //   }
  // };

  // refreshToken을 먼저 실행하고 성공했을 때만 이후 요청 실행
  // const tokenRefreshed = await refreshToken();
  // if (!tokenRefreshed) return;

  try {
    const res = await axios.get(apiUrl, { withCredentials: true });
    const data = res.data;

    console.log(data);

    if (data.status === 'logout') {
      // navigate('/logout');
      return;
    }

    setUser({
      no: data.no || '',
      name: data.name || '',
      email: data.email || '',
      PhotoNo: data.PhotoNo || ''
    });

    if (data.cardPictureUri) {
      setCardImageBaseUri(data.cardPictureUri);
    }

    if (data.cardInfos) {
      setCards(data.cardInfos);
    }
  } catch (err) {
    console.error("데이터 로딩 실패:", err);
  }
};

  // 컴포넌트 마운트 시와 searchKeyword 변경 시 데이터 가져오기
  useEffect(() => {
    fetchData(searchKeyword);
  }, []);

  // 검색 버튼 클릭 시 호출되는 함수
  const searchCards = () => {
    fetchData(searchKeyword);
  };

  const handleInput = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  //명함 입력 탭 등록요청
  const handleRegister = (e) => {
    e.preventDefault();
    
    axios.post(`${API_BASE}/RegCardInfo`, formData, {
      headers: {
        'Content-Type': 'application/json'
      },
      withCredentials: true
    })
    .then((res) => {
      if (res.data.status === true) {
        alert("명함 등록 완료.");
        window.location.href = '/';
      } else {
        alert("명함 등록 오류.");
      }
    })
    .catch((err) => {
      console.error("명함 등록 실패:", err);
      alert("명함 등록 중 오류가 발생했습니다.");
    });
  };

  const handleProfileChange = (e) => {
    const file = e.target.files[0];
    console.log(file);
    if (file) {
      setProfileFile(file);
      const reader = new FileReader();
      reader.onload = (event) => setProfileImage(event.target.result);
      reader.readAsDataURL(file);
    }
  };

  const handleCardChange = (e) => {
    const file = e.target.files[0];
    console.log(file);
    if (file) {
      setCardFile(file);
      const reader = new FileReader();
      reader.onload = (event) => setCardImage(event.target.result);
      reader.readAsDataURL(file);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFormData((prevData) => ({
      ...prevData,
      [name]: value,
    }));
  };

  

  return (
    <Layout setActiveTab={setActiveTab} userNo={user.no} prelogin={!user.no} afterlogin={!!user.no}>
      <div className="container-fluid" style={{ marginLeft: 0, marginRight: 0, marginTop: 35, width: '100%' }}>
        <div className="container-fluid">
          <div className="main-container">
            {user.email && (
              <div className="form-user">
                <img src={user.PhotoNo || defaultProfile} alt="Profile" className="profile-img" />
                <br />
                <div className="username" style={{ fontSize: '1.6em', fontWeight: 'bold' }}>{user.name}</div>
                <div className="user-email" style={{ fontSize: '1.4em', color: '#666' }}>{user.email}</div>
                <br />
                <button className="btn btn-lg btn-primary btn-block btn-center main-regbtn" style={{ width: '250px' }} onClick={() => setActiveTab('writereg')}>
                  명함 등록
                </button>
              </div>
            )}

            <div className="content-wrapper">
              <div className="tabs">
                {['cardlist', 'writereg', 'picturereg'].map((tab) => (
                  <div key={tab} className={`tab ${activeTab === tab ? 'active' : ''}`} onClick={() => setActiveTab(tab)}>
                    {tab === 'cardlist' ? '명함목록' : tab === 'writereg' ? '명함입력' : '사진등록'}
                  </div>
                ))}
              </div>

              {activeTab === 'cardlist' && (
                <div className="tab-content active">
                  <div style={{ margin: '20px 0', display: 'flex', justifyContent: 'flex-start', alignItems: 'center' }}>
                  <input
                    type="text"
                    onKeyDown={(e) => {
                      if (e.key === 'Enter') {
                        e.preventDefault();
                        searchCards();
                      }
                    }}
                    id="searchinput"
                    value={searchKeyword}
                    onChange={(e) => setSearchKeyword(e.target.value)}
                    placeholder="이름, 직위, 회사명으로 명함을 검색 해주세요"
                    style={{
                      padding: '10px 15px',
                      borderRadius: '30px',
                      border: '2px solid #007bff',
                      width: '370px',
                      fontSize: '16px',
                      marginRight: '10px',
                      transition: 'border-color 0.3s ease-in-out',
                      marginBottom: '20px'
                    }}
                  />
                  <button
                    onClick={searchCards}
                    style={{
                      width: '120px',
                      padding: '10px 20px',
                      marginBottom: '20px',
                      backgroundColor: '#007bff',
                      color: 'white',
                      border: 'none',
                      borderRadius: '30px',
                      fontSize: '16px',
                      cursor: 'pointer'
                    }}
                  >
                    검색
                  </button>

                  </div>
                  <div className="post-list" style={{ display: 'flex', flexWrap: 'wrap', gap: '15px' }}>
                    {cards.map((card) => (
                      <div 
                        key={card.no} 
                        className="post-card"                       
                        data-card-no={card.no} 
                        onClick={(e) => {
                          const cardNo = e.currentTarget.getAttribute("data-card-no");
                          navigate(`/detail/${cardNo}`);
                        }}
                        style={{ width: '320px', display: 'flex', flexDirection: 'column', alignItems: 'center', padding: '20px', textAlign: 'center', position: 'relative' }}>
                        <img
                          className="profile-img"
                          src={card.fileUserNo === 0 ? defaultProfile : `${cardImageBaseUri}${card.fileUserNo}`}
                          alt="프로필"
                          style={{ width: '100px', height: '100px', marginBottom: '15px' }}
                        />
                        <button
                          className="delete-btn"
                          onClick={(event) => handleDelete(card.no, event)}
                          style={{ position: 'absolute', top: '10px', right: '10px', background: 'transparent', border: 'none', fontSize: '20px', color: '#007bff', cursor: 'pointer', fontWeight: 'bold' }}
                        >&times;</button>
                        <div className="username" style={{ fontSize: '1.4em', fontWeight: 'bold', color: '#333' }}>{card.name}</div>
                        <div className="position" style={{ fontSize: '1.1em', fontWeight: 500, color: '#007bff', marginBottom: '10px' }}>{card.position}</div>
                        
                        {card.filePictureNo && card.filePictureNo !== 0 ? (
                          // filePictureNo가 있을 때 (명함 이미지 표시)
                          <div style={{ width: '100%', minHeight: '120px', textAlign: 'left', padding: '10px', background: '#f8f9fa', borderRadius: '10px' }}>
                            <div className="cardreg-img-container">
                              <img
                                src={`${cardImageBaseUri}${card.filePictureNo}`}
                                alt="Profile Picture"
                                className="cardreg-img"
                              />
                            </div>
                          </div>
                        ) : (
                          // filePictureNo가 없을 때 (연락처 정보 표시)
                          <div style={{ width: '100%', minHeight: '120px', textAlign: 'left', padding: '10px', background: '#f8f9fa', borderRadius: '10px' }}>
                            <p style={{ margin: '5px 0' }}><strong>Tel:</strong> {card.tel}</p>
                            <p style={{ margin: '5px 0' }}><strong>Fax:</strong> {card.fax}</p>
                            <p style={{ margin: '5px 0' }}><strong>Email:</strong> {card.email}</p>
                            <p style={{ margin: '5px 0' }}><strong>Address:</strong> {card.adr}</p>
                          </div>
                        )}

                      </div>
                    ))}
                  </div>
                </div>
              )}

              {activeTab === 'writereg' && (
                <div className="tab-content writereg-tab-content active" style={{ marginLeft: '20px', marginRight: '20px' }}>
                  <form style={{ width: '100%' }} onSubmit={handleRegister}>
                    <div className="form-row">
                      <div className="form-column form-group">
                        <label htmlFor="company">회사명</label>
                        <input type="text" id="company" name="company" value={formData.company} onChange={handleInput} className="form-control" placeholder="회사명을 입력하세요" />
                      </div>
                      <div className="form-column form-group">
                        <label htmlFor="position">포지션</label>
                        <input type="text" id="position" name="position" value={formData.position} onChange={handleInput} className="form-control" placeholder="직위를 입력하세요" />
                      </div>
                    </div>
                    <div className="form-row">
                      <div className="form-column form-group">
                        <label htmlFor="name">이름 <span style={{ color: 'red' }}>*</span></label>
                        <input type="text" id="name" name="name" required value={formData.name} onChange={handleInput} className="form-control" placeholder="이름을 입력하세요" />
                      </div>
                      <div className="form-column form-group">
                        <label htmlFor="adr">주소</label>
                        <input type="text" id="adr" name="adr" value={formData.adr} onChange={handleInput} className="form-control" placeholder="주소를 입력하세요" />
                      </div>
                    </div>
                    <div className="form-row">
                      <div className="form-column form-group">
                        <label htmlFor="email">메일</label>
                        <input type="email" id="email" name="email" value={formData.email} onChange={handleInput} className="form-control" placeholder="이메일을 입력하세요" />
                      </div>
                      <div className="form-column form-group">
                        <label htmlFor="tel">전화 <span style={{ color: 'red' }}>*</span></label>
                        <input type="tel" id="tel" name="tel" required value={formData.tel} onChange={handleInput} className="form-control" placeholder="전화번호를 입력하세요" />
                      </div>
                    </div>
                    <div className="form-row">
                      <div className="form-column form-group">
                        <label htmlFor="fax">팩스</label>
                        <input type="tel" id="fax" name="fax" value={formData.fax} onChange={handleInput} className="form-control" placeholder="팩스번호를 입력하세요" />
                      </div>
                      <div className="form-column form-group">
                        <label htmlFor="memo">메모</label>
                        <input id="memo" name="memo" value={formData.memo} onChange={handleInput} className="form-control" placeholder="메모를 입력하세요" />
                      </div>
                    </div>
                    <div className="more-button-container" style={{ display: 'flex', justifyContent: 'center', marginTop: '20px' }}>
                      <button type="submit" className="btn btn-lg btn-primary" style={{ width: '250px', marginTop: '20px' }}>명함 등록</button>
                    </div>
                  </form>
                </div>
              )}

              {activeTab === 'picturereg' && (
                <div className="tab-content active">
                  <div className="post-list" style={{ display: 'flex', justifyContent: 'center', alignItems: 'center' }}>
                    <form onSubmit={handleSubmit} style={{ maxWidth: '500px', width: '100%', padding: '20px', display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                      <div className="picture-container" style={{ display: 'flex', flexWrap: 'wrap', justifyContent: 'center' }}>
                        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center' }}>
                          <label>(프로필 사진)</label>
                          <img src={profileImage || defaultProfile} alt="Profile" className="profile-img" style={{ cursor: 'pointer', width: '150px' }} onClick={() => document.getElementById('file-input-profile').click()} />
                          <input type="file" id="file-input-profile" accept="image/*" onChange={handleProfileChange} style={{ display: 'none' }} />
                        </div>
                        <div style={{ display: 'flex', flexDirection: 'column', alignItems: 'center', width: '200px' }}>
                          <label><span style={{ color: 'red' }}>*</span> 명함 사진 등록</label>
                          <img src={cardImage} alt="Card" className="cardreg-img" style={{ cursor: 'pointer', width: '150px' }} onClick={() => fileInputRef.current.click()} />
                          <input type="file" id="file-input-card" ref={fileInputRef} accept="image/*" onChange={handleCardChange} style={{ display: 'none' }} />
                        </div>
                      </div>
                      <div className="form-group" style={{ display: 'flex', alignItems: 'center', gap: '10px', marginTop: '20px' }}>
                        <label style={{ whiteSpace: 'nowrap', paddingTop: '10px' }}><span style={{ color: 'red' }}>*</span> 이름 :</label>
                        <input type="text" name="name" value={formData.name} onChange={handleInputChange} className="form-control"  placeholder="이름을 입력하세요" required style={{ flex: 1, maxWidth: '400px' }} />
                      </div>
                      <div className="form-group" style={{ display: 'flex', alignItems: 'center', gap: '10px', marginTop: '10px' }}>
                        <label style={{ whiteSpace: 'nowrap', paddingTop: '10px' }}><span style={{ color: 'red' }}>*</span> 전화 :</label>
                        <input type="tel" name="tel" value={formData.tel} onChange={handleInputChange} className="form-control" placeholder="전화번호를 입력하세요" required style={{ flex: 1, maxWidth: '400px' }} />
                      </div>
                      <button type="submit" className="btn btn-lg btn-primary" style={{ width: '250px', marginTop: '20px' }}>명함 등록</button>
                    </form>
                  </div>
                </div>
              )}
            </div>
          </div>
        </div>
      </div>
    </Layout>
  );
}
