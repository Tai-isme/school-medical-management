import React, { useState, useEffect } from "react";
import axios from "axios";

const mockProfile = {
  name: "Lê Hải Anh",
  code: "HS_00147",
  gender: "Nam",
  dob: "15/03/2019",
  placeOfBirth: "Bệnh viện Nhi",
  healthType: "2",
  class: "Cơm nát",
  avatar: "https://via.placeholder.com/120x120?text=Avatar",
};

const mockChronic = {
  type: "Dị ứng",
  status: "Khi ăn đậu hoặc đồ ăn liên quan đến đậu thì bị nổi mẩn đỏ.",
  solution: "Hạn chế món đồ ăn có đậu cho bé.",
};

const mockVaccines = [
  { name: "Hepatitis B", desc: "Đã tiêm ở tại trường" },
  { name: "1", desc: "1" },
  { name: "2", desc: "2" },
  { name: "3", desc: "3" },
];

export default function StudentProfileCard({ studentId, studentInfo }) {
  const [tab, setTab] = useState("chronic");
  const [record, setRecord] = useState(null);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!studentId) return;
    setLoading(true);
    const fetchRecord = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get(
          `http://localhost:8080/api/admin/medical-records/${studentId}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        setRecord(res.data);
      } catch (err) {
        setRecord(null);
      } finally {
        setLoading(false);
      }
    };
    fetchRecord();
  }, [studentId]);

  if (!studentId) return <div>Chọn học sinh để xem hồ sơ</div>;
  if (loading) return <div>Đang tải...</div>;

  // Nếu record là null hoặc undefined, tạo object rỗng với các thuộc tính mặc định
  const safeRecord = record || {
    vision: "",
    hearing: "",
    weight: "",
    height: "",
    allergies: "",
    chronicDisease: "",
    treatmentHistory: "",
    note: "",
    vaccineHistories: [],
    studentId: "",
  };

  return (
    <div style={{ background: "#fff", borderRadius: 8, padding: "8px", minHeight: 180, width: "800px"}}>
      <div style={{ display: "flex", alignItems: "center", gap: 32, marginBottom: 16, minHeight: 120, height: 120 }}>
        <img
          src="./logo512.png"
          alt="avatar"
          style={{
            width: 120,
            height: 120,
            borderRadius: "50%",
            objectFit: "cover",
            border: "2px solid #e0e0e0",
            background: "#fafafa",
          }}
        />
        <div style={{ display: "flex", flexDirection: "column", justifyContent: "center", height: "100%" }}>
          <div style={{ fontWeight: "bold", fontSize: 28, marginBottom: 6 }}>
            Mã số học sinh: {safeRecord.studentId}
          </div>
          <div style={{ fontSize: 20, marginBottom: 2 }}>
            Tên học sinh: {studentInfo?.fullName || ""}
          </div>
          <div style={{ fontSize: 18, marginBottom: 2 }}>
            Lớp: {studentInfo?.classDTO?.className || ""}
          </div>
          <div style={{ fontSize: 18 }}>
            Giới tính: {studentInfo?.gender || ""}
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div
        style={{
          background: "#f8fafd",
          borderRadius: 8,
          // marginTop: 8,
          padding: 16,
          border: "1px solid #e0e0e0",
          maxWidth: 768,
        }}
      >
        <div style={{ display: "flex", alignItems: "center", marginBottom: 12 }}>
          <div
            onClick={() => setTab("chronic")}
            style={{
              fontWeight: "bold",
              fontSize: 15,
              borderBottom: tab === "chronic" ? "2px solid #bdbdbd" : "none",
              paddingBottom: 2,
              marginRight: 18,
              color: tab === "chronic" ? "#888" : "#bbb",
              cursor: "pointer",
            }}
          >
            Hồ sơ sức khỏe
          </div>
          <div
            onClick={() => setTab("vaccine")}
            style={{
              fontWeight: "bold",
              fontSize: 15,
              borderBottom: tab === "vaccine" ? "2px solid #4caf50" : "none",
              paddingBottom: 2,
              color: tab === "vaccine" ? "#388e3c" : "#bbb",
              cursor: "pointer",
            }}
          >
            Các Vaccine đã tiêm
          </div>
        </div>

        {/* Tab content */}
        {tab === "chronic" && (
          <div
            style={{
              background: "#f9fbfd",
              borderRadius: 8,
              padding: "0px",
              width: "100%",
              boxSizing: "border-box",
              maxWidth: 700,
              margin: "0 auto"
            }}
          >
            <div style={{ textAlign: "center", fontWeight: "bold", fontSize: 22, margin: "0px 0px 8px 0px"  }}>
              Thông tin học sinh
            </div>
            <div style={{ display: "flex", gap: 20, marginBottom: 18 }}>
              <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
                <label style={{ marginBottom: 8, fontSize: 18 }}>Thị giác <span style={{ color: "red" }}>*</span></label>
                <input style={{ width: "100%", padding: 10, borderRadius: 5, border: "1px solid #ccc", fontSize: 18 }} value={safeRecord.vision || ""} readOnly />
              </div>
              <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
                <label style={{ marginBottom: 8, fontSize: 18 }}>Thính lực <span style={{ color: "red" }}>*</span></label>
                <input style={{ width: "100%", padding: 10, borderRadius: 5, border: "1px solid #ccc", fontSize: 18 }} value={safeRecord.hearing || ""} readOnly />
              </div>
            </div>
            <div style={{ display: "flex", gap: 20, marginBottom: 18 }}>
              <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
                <label style={{ marginBottom: 8, fontSize: 18 }}>Cân nặng <span style={{ color: "red" }}>*</span></label>
                <input style={{ width: "100%", padding: 10, borderRadius: 5, border: "1px solid #ccc", fontSize: 18 }} value={safeRecord.weight || ""} readOnly />
              </div>
              <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
                <label style={{ marginBottom: 8, fontSize: 18 }}>Chiều cao <span style={{ color: "red" }}>*</span></label>
                <input style={{ width: "100%", padding: 10, borderRadius: 5, border: "1px solid #ccc", fontSize: 18 }} value={safeRecord.height || ""} readOnly />
              </div>
            </div>
            <div style={{ marginBottom: 18 }}>
              <label style={{ marginBottom: 8, display: "block", fontSize: 18 }}>Bị dị ứng với các loại nào</label>
              <textarea style={{ width: "100%", padding: 10, borderRadius: 5, border: "1px solid #ccc", fontSize: 18, minHeight: 40 }} value={safeRecord.allergies || ""} readOnly />
            </div>
            <div style={{ marginBottom: 18 }}>
              <label style={{ marginBottom: 8, display: "block", fontSize: 18 }}>Bệnh mãn tính</label>
              <textarea style={{ width: "100%", padding: 10, borderRadius: 5, border: "1px solid #ccc", fontSize: 18, minHeight: 40 }} value={safeRecord.chronicDisease || ""} readOnly />
            </div>
            <div style={{ marginBottom: 18 }}>
              <label style={{ marginBottom: 8, display: "block", fontSize: 18 }}>Lịch sử điều trị</label>
              <textarea style={{ width: "100%", padding: 10, borderRadius: 5, border: "1px solid #ccc", fontSize: 18, minHeight: 40 }} value={safeRecord.treatmentHistory || ""} readOnly />
            </div>
            <div>
              <label style={{ marginBottom: 8, display: "block", fontSize: 18 }}>Ghi chú</label>
              <textarea style={{ width: "100%", padding: 10, borderRadius: 5, border: "1px solid #ccc", fontSize: 18, minHeight: 40 }} value={safeRecord.note || ""} readOnly />
            </div>
          </div>
        )}

        {tab === "vaccine" && (
          <div style={{ background: "#f9fbfd", borderRadius: 8, padding: 12 }}>
            <div style={{ textAlign: "center", fontWeight: "bold", fontSize: 22, marginBottom: 12 }}>
              Các loại vaccin đã tiêm
            </div>
            <div style={{ display: "flex", fontWeight: "bold", marginBottom: 8 }}>
              <div style={{ flex: 1, textAlign: "left" }}>Tên Vaccin</div>
              <div style={{ flex: 1, textAlign: "left" }}>Mô tả</div>
            </div>
            {(safeRecord.vaccineHistories || []).map((v, idx) => (
              <div key={idx} style={{ display: "flex", gap: 8, marginBottom: 8 }}>
                <input
                  style={{
                    flex: 1,
                    padding: 8,
                    borderRadius: 5,
                    border: "1px solid #ccc",
                    fontSize: 16,
                    fontFamily: "inherit"
                  }}
                  value={v.vaccineName.vaccineName || ""}
                  readOnly
                />
                <input
                  style={{
                    flex: 1,
                    padding: 8,
                    borderRadius: 5,
                    border: "1px solid #ccc",
                    fontSize: 16,
                    fontFamily: "inherit"
                  }}
                  value={v.note || ""}
                  readOnly
                />
              </div>
            ))}
          </div>
        )}
      </div>
    </div>
  );
}