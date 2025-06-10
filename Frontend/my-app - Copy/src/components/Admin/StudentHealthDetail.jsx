import { useState } from "react";

export default function StudentHealthDetail() {
  const [activeTab, setActiveTab] = useState("thongtin");

  // Dữ liệu mẫu cứng với tất cả trường là "1"
  const student = {
    name: "1",
    id: "1",
    dob: "1",
    gender: "1",
    health: {
      chronicType: "1",
      description: "1",
      solution: "1"
    },
    incident: {
      type: "1",
      date: "1",
      action: "1"
    },
    emergency: {
      name: "1",
      relation: "1",
      phone: "1",
      address: "1"
    }
  };

  const renderTab = () => {
    switch (activeTab) {
      case "thongtin":
        return (
          <div className="section">
            <h3>Thông tin cá nhân</h3>
            <p><strong>Họ tên:</strong> {student.name}</p>
            <p><strong>Mã HS:</strong> {student.id}</p>
            <p><strong>Ngày sinh:</strong> {student.dob}</p>
            <p><strong>Giới tính:</strong> {student.gender}</p>
          </div>
        );
      case "benhmantinh":
        return (
          <div className="section">
            <h3>Bệnh mạn tính</h3>
            <p><strong>Loại:</strong> {student.health.chronicType}</p>
            <p><strong>Triệu chứng:</strong> {student.health.description}</p>
            <p><strong>Xử lý:</strong> {student.health.solution}</p>
          </div>
        );
      case "sucoyte":
        return (
          <div className="section">
            <h3>Sự cố y tế</h3>
            <p><strong>Loại:</strong> {student.incident.type}</p>
            <p><strong>Ngày:</strong> {student.incident.date}</p>
            <p><strong>Xử lý:</strong> {student.incident.action}</p>
          </div>
        );
      case "lienhe":
        return (
          <div className="section">
            <h3>Liên hệ khẩn cấp</h3>
            <p>{student.emergency.name} ({student.emergency.relation})</p>
            <p>📞 {student.emergency.phone}</p>
            <p>🏠 {student.emergency.address}</p>
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
