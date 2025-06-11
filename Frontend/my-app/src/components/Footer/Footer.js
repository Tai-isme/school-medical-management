// Footer.js
import React from 'react';
import './Footer.css'; // Import CSS file

const Footer = () => {
  return (
    <footer className="footer-container">
      <div className="footer-content">
        <div className="ministry-info">
          <img src="/path/to/your/logo.png" alt="Logo Phòng Giáo dục và Đào tạo TP. Thủ Đức" className="ministry-logo" />
          <div className="ministry-text">
            <p className="ministry-name">Phòng Giáo dục và Đào tạo thành phố Thủ Đức</p>
            <p className="ministry-address">Địa chỉ: 04 Nguyễn Công Trứ, phường Bình Thọ, Tp. Thủ Đức, Tp. Hồ Chí Minh</p>
          </div>
        </div>

        <div className="copyright-section">
          <p className="copyright-text">
            © Bản quyền thuộc Phòng Giáo dục và Đào tạo thành phố Thủ Đức -
            <a href="http://pgdtdc.hcm.edu.vn" target="_blank" rel="noopener noreferrer"> pgdtdc.hcm.edu.vn</a>
          </p>
          <div className="powered-by">
            <span className="powered-by-text">Powered by</span>
            <img src="/path/to/your/qkgroup-logo.png" alt="QK Group Logo" className="qkgroup-logo" />
          </div>
        </div>
      </div>

      <a href="mailto:PgdtpThuDuc@hcm.edu.vn" className="email-button">
        <img src="/path/to/your/email-icon.png" alt="Email Icon" className="email-icon" />
        PgdtpThuDuc@hcm.edu.vn
      </a>
    </footer>
  );
};

export default Footer;