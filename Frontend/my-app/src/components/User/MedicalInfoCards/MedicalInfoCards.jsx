import React from 'react';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';
import { faNotesMedical, faHouseMedical, faSuitcaseMedical, faHouseMedicalCircleExclamation, faSyringe, faStethoscope, faChartLine } from '@fortawesome/free-solid-svg-icons';
import './MedicalInfoCards.css';

const MedicalInfoCards = (props) => {
  return (
    <div className="medical-info-container">
      <h1 className="main-title" style={{marginTop: '0px'}}>Thông tin Y tế học đường</h1>
      <div className="cards-grid">
        {/* Card 1: Gửi thuốc */}
        <div className="info-card" onClick={() => props.onCardClick('/instruction-form')} style={{ cursor: 'pointer' }}>
          <div className="card-icon-wrapper">
            <FontAwesomeIcon icon={faNotesMedical} style={{ fontSize: '32px', color: '#64b5f6' }} />
          </div>
          <h2 className="card-title">Gửi thuốc</h2>
          <p className="card-description">Tạo yêu cầu và hướng dẫn nhân viên y tế của trường sử dụng thuốc cho học sinh</p>
        </div>

        {/* Card 2: Quản lý hồ sơ */}
        <div className="info-card" onClick={() => props.onCardClick('/student-profile')} style={{ cursor: 'pointer' }}>
          <div className="card-icon-wrapper">
            <FontAwesomeIcon icon={faHouseMedical} style={{ fontSize: '32px', color: '#64b5f6' }} />
          </div>
          <h2 className="card-title">Quản lý hồ sơ</h2>
          <p className="card-description">Xem/Cập nhật hồ sơ sức khỏe của học sinh</p>
        </div>

        {/* Card 3: Sự kiện y tế */}
        <div className="info-card" onClick={() => props.onCardClick('/medical-incident')} style={{ cursor: 'pointer' }}>
          <div className="card-icon-wrapper">
            <FontAwesomeIcon icon={faSuitcaseMedical} style={{ fontSize: '32px', color: '#64b5f6' }} />
          </div>
          <h2 className="card-title">Sự kiện y tế</h2>
          <p className="card-description">Kiểm tra học sinh có dấu hiệu về sức khỏe khi ở trường</p>
        </div>

        {/* Card 4: Xác nhận yêu cầu */}
        <div className="info-card" onClick={() => props.onCardClick('/notification')} style={{ cursor: 'pointer' }}>
          <div className="card-icon-wrapper">
            <FontAwesomeIcon icon={faHouseMedicalCircleExclamation} style={{ fontSize: '32px', color: '#64b5f6' }} />
          </div>
          <h2 className="card-title">Xác nhận yêu cầu</h2>
          <p className="card-description">Kiểm tra và xác nhận các đơn từ nhà trường (khám định kỳ, đăng ký tiêm chủng...)</p>
        </div>

        {/* Card 5: Kiểm tra kết quả vaccin */}
        <div className="info-card" onClick={() => props.onCardClick('/vaccine-result')} style={{ cursor: 'pointer' }}>
          <div className="card-icon-wrapper">
            <FontAwesomeIcon icon={faSyringe} style={{ fontSize: '32px', color: '#64b5f6' }} />
          </div>
          <h2 className="card-title">Kiểm tra kết quả vaccin</h2>
          <p className="card-description">Xem kết quả tiêm chủng và lịch sử vaccin của học sinh</p>
        </div>

        {/* Card 6: Kiểm tra sức khỏe */}
        <div className="info-card" onClick={() => props.onCardClick('/health-check')} style={{ cursor: 'pointer' }}>
          <div className="card-icon-wrapper">
            <FontAwesomeIcon icon={faStethoscope} style={{ fontSize: '32px', color: '#64b5f6' }} />
          </div>
          <h2 className="card-title">Kiểm tra sức khỏe</h2>
          <p className="card-description">Kiểm tra và theo dõi sức khỏe tổng quát của học sinh</p>
        </div>
      </div>
    </div>
  );
};

export default MedicalInfoCards;