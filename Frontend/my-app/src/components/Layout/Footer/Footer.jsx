import React from "react";

export default function Footer() {
  return (
    <footer
      style={{
        background: "linear-gradient(90deg, #e3f2fd 0%, #90caf9 100%)",
        color: "#1976d2",
        padding: "32px 0 16px 0",
        marginTop: 48,
        textAlign: "center",
        boxShadow: "0 -4px 24px #1976d220",
        fontFamily: "'Poppins', 'Roboto', sans-serif"
      }}
    >
      <div style={{ maxWidth: 1100, margin: "0 auto" }}>
        
        <h2 style={{ margin: "0 0 8px 0", fontWeight: 600 }}>
          Y Tế Học Đường
        </h2>
        <p style={{ margin: "0 0 18px 0", color: "#1565c0" }}>
          Nâng cao sức khỏe học sinh – Chung tay vì tương lai Việt Nam
        </p>
        <div style={{ marginBottom: 12 }}>
          <a
            href="mailto:support@ytehocduong.vn"
            style={{
              color: "#1976d2",
              textDecoration: "none",
              margin: "0 12px",
              fontWeight: 500
            }}
          >
            support@ytehocduong.vn
          </a>
          |
          <span style={{ margin: "0 12px" }}>
            ĐT: 0123 456 789
          </span>
          |
          <span style={{ margin: "0 12px" }}>
            Địa chỉ: 123 Đường Sức Khỏe, Quận 1, TP.HCM
          </span>
        </div>
        <div style={{ fontSize: 14, color: "#1976d2a0" }}>
          © {new Date().getFullYear()} Y Tế Học Đường. All rights reserved.
        </div>
      </div>
    </footer>
  );
}