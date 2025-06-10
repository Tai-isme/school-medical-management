import { useState } from "react";

export default function StudentHealthDetail({ student }) {
  const [activeTab, setActiveTab] = useState("thongtin");

  const renderTab = () => {
    switch (activeTab) {
      case "thongtin":
        return (
          <div className="section">
            {/* Thông tin cá nhân sẽ được truyền từ API vào đây */}
          </div>
        );
      case "benhmantinh":
        return (
          <div className="section">
            {/* Bệnh mạn tính sẽ được truyền từ API vào đây */}
          </div>
        );
      case "sucoyte":
        return (
          <div className="section">
            {/* Sự cố y tế sẽ được truyền từ API vào đây */}
          </div>
        );
      case "lienhe":
        return (
          <div className="section">
            {/* Liên hệ khẩn cấp sẽ được truyền từ API vào đây */}
          </div>
        );
      default:
        return null;
    }
  };

  return (
    <div className="student-detail">
      <div className="tab-buttons" style={{ marginBottom: "16px" }}>
        <button onClick={() => setActiveTab("thongtin")}>Thông tin</button>
        <button onClick={() => setActiveTab("benhmantinh")}>Bệnh mạn tính</button>
        <button onClick={() => setActiveTab("sucoyte")}>Sự cố</button>
        <button onClick={() => setActiveTab("lienhe")}>Liên hệ</button>
      </div>
      {renderTab()}
    </div>
  );
}