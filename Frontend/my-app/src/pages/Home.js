import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar/Navbar';
import MedicalInfoCards from '../components/MedicalInfoCards/MedicalInfoCards';
import Login from './Login/Login'; // import Login

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
      <MedicalInfoCards onCardClick={handleCardClick} />
      {showLogin && <Login onClose={() => setShowLogin(false)} />}
    </div>
    
  );
}

export default Home;