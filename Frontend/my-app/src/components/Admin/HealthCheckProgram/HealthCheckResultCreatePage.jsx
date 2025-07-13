import React, { useEffect, useState } from "react";
import { useParams } from "react-router-dom";
import CreateHealthCheckResultModal from "./CreateHealthCheckResultModal"; // hoặc copy Table code vào đây

const HealthCheckResultCreatePage = () => {
  const { programId } = useParams();
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    const fetchData = async () => {
      setLoading(true);
      const token = localStorage.getItem("token");
      const res = await fetch("http://localhost:8080/api/nurse/health-check-result", {
        headers: { Authorization: `Bearer ${token}` }
      });
      const data = await res.json();
      const filtered = data.filter(item =>
        item.healthCheckFormDTO?.healthCheckProgram?.id === Number(programId)
      );
      setStudents(filtered.map(item => ({
        studentId: item.studentDTO?.id,
        studentName: item.studentDTO?.fullName,
        diagnosis: item.diagnosis || "",
        level: item.level || "",
        note: item.note || "",
        vision: item.vision || "",
        hearing: item.hearing || "",
        weight: item.weight || "",
        height: item.height || ""
      })));
      setLoading(false);
    };
    fetchData();
  }, [programId]);

  // ...handle change, save, etc.

  return (
    <div style={{ padding: 24 }}>
      <h2>Tạo kết quả khám định kỳ</h2>
      <CreateHealthCheckResultModal
        open={true}
        onCancel={() => window.history.back()}
        students={students}
        onChange={(value, idx, field) => {
          setStudents(prev =>
            prev.map((item, i) => i === idx ? { ...item, [field]: value } : item)
          );
        }}
        onOk={() => {/* Gửi dữ liệu lên backend */}}
        loading={loading}
      />
    </div>
  );
};

export default HealthCheckResultCreatePage;