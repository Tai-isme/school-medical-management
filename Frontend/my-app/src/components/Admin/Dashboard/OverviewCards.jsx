import React, { useEffect, useState } from "react";
import axios from "axios";
import "./DashboardWidgets.css";

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
    <div className="overview-grid">
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
    <div className="stat-card">
      <div className="stat-title">{title}</div>
      <div className="stat-value">{value}</div>
    </div>
  );
}

export default OverviewCards;
