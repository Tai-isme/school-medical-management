import React, { useEffect, useState } from "react";
import axios from "axios";

function OverviewCards() {
  const [studentCount, setStudentCount] = useState(0);
  const [medicalRecordCount, setMedicalRecordCount] = useState(0);
  const [vaccineProgramCount, setVaccineProgramCount] = useState(0);  
  const [healthCheckProgramCount, setHealthCheckProgramCount] = useState(0);
  const [processingMedicalRequestCount, setProcessingMedicalRequestCount] = useState(0);

  useEffect(() => {
    const token = localStorage.getItem("token");
    axios.get("http://localhost:8080/api/admin/statistics/overview", {
      headers: { Authorization: `Bearer ${token}` }
    }).then(res => {
      setStudentCount(res.data.studentCount);
      setMedicalRecordCount(res.data.medicalRecordCount);
      setVaccineProgramCount(res.data.vaccineProgramCount);
      setHealthCheckProgramCount(res.data.healthCheckProgramCount);
      setProcessingMedicalRequestCount(res.data.processingMedicalRequestCount);
    }).catch(err => {
      console.error("Lỗi lấy dữ liệu tổng quan:", err);
    });
  }, []);

  return (
    <div
      style={{
        display: "grid",
        gridTemplateColumns: "repeat(5, 1fr)",
        gap: "20px",
        marginBottom: "30px"
      }}
    >
      <StatCard title="👩‍🎓 Tổng số học sinh" value={studentCount} />
      <StatCard title="📄 Tổng hồ sơ y tế" value={medicalRecordCount} />
      <StatCard title="💉 Chương trình vaccine hiện tại" value={vaccineProgramCount} />
      <StatCard title="🩺 Chương trình khám sức khỏe hiện tại" value={healthCheckProgramCount} />
      <StatCard title="📨 Yêu cầu gửi thuốc chờ xử lý" value={processingMedicalRequestCount} />
    </div>
  );
}

function StatCard({ title, value }) {
  return (
    <div style={cardStyle}>
      <div style={titleStyle}>{title}</div>
      <div style={valueStyle}>{value}</div>
    </div>
  );
}

const cardStyle = {
  background: "#ffffff",
  borderRadius: "12px",
  padding: "20px",
  boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
  textAlign: "center",
  display: "flex",
  flexDirection: "column",
  justifyContent: "center"
};

const titleStyle = {
  fontSize: "14px",
  color: "#00796b",
  marginBottom: "8px",
  fontWeight: "bold"
};

const valueStyle = {
  fontSize: "22px",
  fontWeight: "bold",
  color: "#004d40"
};

export default OverviewCards;
