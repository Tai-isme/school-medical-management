import React from 'react';
import { useNavigate } from 'react-router-dom';
import Navbar from '../components/Navbar/Navbar';
import MedicalInfoCards from '../components/MedicalInfoCards/MedicalInfoCards';

function Home() {
  const navigate = useNavigate();

  // Hàm xử lý khi click vào thẻ
  const handleCardClick = (page) => {
    navigate(page);
  };

  return (
    <div>
      <Navbar />
      <MedicalInfoCards onCardClick={handleCardClick} />
    </div>
  );
}

export default Home;