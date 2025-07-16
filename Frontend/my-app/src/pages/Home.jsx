import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import MedicalInfoCards from '../components/User/MedicalInfoCards/MedicalInfoCards';
import Login from '../components/Layout/Login/Login.jsx';
import Slider from '../components/Layout/Slider/Slider';

function Home() {
  const navigate = useNavigate();
  const [showLogin, setShowLogin] = useState(false);

  // Kiểm tra đăng nhập
  const isLoggedIn = !!localStorage.getItem("users");

  // Hàm xử lý khi click vào thẻ
  const handleCardClick = (page) => {
    if (!isLoggedIn) {
      setShowLogin(true); // Mở popup login
      return;
    }
    navigate(page);
  };

  return (
    <div>
      <Slider />
      <MedicalInfoCards onCardClick={handleCardClick} />
      {showLogin && <Login onClose={() => setShowLogin(false)} />}
    </div>
    
  );
}

export default Home;