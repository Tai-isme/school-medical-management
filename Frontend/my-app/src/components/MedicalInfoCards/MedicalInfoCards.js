import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome'; // Import component FontAwesomeIcon
import '../MedicalInfoCards/MedicalInfoCards.css'; // Import CSS cho component này


const MedicalInfoCards = (props) => {
  return (
    <div className="medical-info-container">
      <h1 className="main-title">Thông tin Y tế học đường</h1>
      <div className="cards-grid">
        {/* Card 1: Gửi thuốc */}
        <div className="info-card" onClick={() => props.onCardClick('/instruction-form')} style={{ cursor: 'pointer' }}>
          <div className="card-icon-wrapper">
            <FontAwesomeIcon icon="notes-medical" style={{ fontSize: '32px', color: '#fff' }} />
          </div>
          <h2 className="card-title">Gửi thuốc</h2>
          <p className="card-description">Tạo yêu cầu và hướng dẫn nhân viên y tế của trường sử dụng thuốc cho học sinh</p>
        </div>

        {/* Card 2: Quản lý hồ sơ */}
        <div className="info-card" onClick={() => props.onCardClick('/student-profile')} style={{ cursor: 'pointer' }}>
          <div className="card-icon-wrapper">
            <FontAwesomeIcon icon="house-medical" style={{ fontSize: '32px', color: '#fff' }} />

          </div>
          <h2 className="card-title">Quản lý hồ sơ</h2>
          <p className="card-description">Xem/Cập nhật hồ sơ sức khỏe của học sinh</p>
        </div>

        {/* Card 3: Sự kiện y tế */}
        <div className="info-card" onClick={() => props.onCardClick('/medical-incident')} style={{ cursor: 'pointer' }}>
          <div className="card-icon-wrapper">
            <FontAwesomeIcon icon="suitcase-medical" style={{ fontSize: '32px', color: '#fff' }} />
          </div>
          <h2 className="card-title">Sự kiện y tế</h2>
          <p className="card-description">Kiểm tra học sinh có dấu hiệu về sức khỏe khi ở trường</p>
        </div>

        {/* Card 4: Xác nhận yêu cầu */}
        <div className="info-card" onClick={() => props.onCardClick('/notification')} style={{ cursor: 'pointer' }}>
          <div className="card-icon-wrapper">
            <FontAwesomeIcon icon="house-medical-circle-exclamation" style={{ fontSize: '32px', color: '#fff' }} />
          </div>
          <h2 className="card-title">Xác nhận yêu cầu</h2>
          <p className="card-description">Kiểm tra và xác nhận các đơn từ nhà trường (khám định kỳ, đăng ký tiêm chủng...)</p>
        </div>
      </div>
    </div>
  );
};

export default MedicalInfoCards;