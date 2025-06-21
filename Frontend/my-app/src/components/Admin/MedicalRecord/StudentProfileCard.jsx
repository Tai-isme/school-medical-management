import React, { useState } from "react";

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

export default function StudentProfileCard({ student }) {
  const [tab, setTab] = useState("chronic");
  const profile = student || mockProfile;
  const chronic = mockChronic;

  return (
    <div style={{ background: "#fff", borderRadius: 8, padding: 16, minHeight: 180, width: "700px"}}>
      <div
        style={{
          display: "flex",
          alignItems: "center",
          gap: 32,
          marginBottom: 16,
          minHeight: 120,
          height: 120,
        }}
      >
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
          <div style={{ fontWeight: "bold", fontSize: 28, marginBottom: 6 }}>Tên: {profile.name}</div>
          <div style={{ color: "#009688", fontWeight: "bold", fontSize: 20, marginBottom: 6 }}>Mã số: {profile.code}</div>
          <div style={{ fontSize: 16, color: "#444" }}>
            <span>Lớp: {profile.class}</span>
          </div>
          <div style={{ fontSize: 16, color: "#444", marginBottom: 2 }}>
            <span>Giới tính: {profile.gender}</span>
            <span style={{ margin: "0 12px" }}>|</span>
            <span>Ngày sinh: {profile.dob}</span>
          </div>
        </div>
      </div>

      {/* Tabs */}
      <div
        style={{
          background: "#f8fafd",
          borderRadius: 8,
          marginTop: 8,
          padding: 16,
          border: "1px solid #e0e0e0",
          maxWidth: 700,
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
              padding: 24,
              width: "100%",
              boxSizing: "border-box",
              maxWidth: 700,
              margin: "0 auto"
            }}
          >
            <div style={{ textAlign: "center", fontWeight: "bold", fontSize: 22, marginBottom: 24 }}>
              Thông tin học sinh
            </div>
            <div style={{ display: "flex", gap: 20, marginBottom: 18 }}>
              <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
                <label style={{ marginBottom: 6 }}>Thị giác <span style={{ color: "red" }}>*</span></label>
                <input style={{ width: "100%", padding: 8, borderRadius: 5, border: "1px solid #ccc", fontSize: 16 }} defaultValue="1" />
              </div>
              <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
                <label style={{ marginBottom: 6 }}>Thính lực <span style={{ color: "red" }}>*</span></label>
                <input style={{ width: "100%", padding: 8, borderRadius: 5, border: "1px solid #ccc", fontSize: 16 }} defaultValue="1" />
              </div>
            </div>
            <div style={{ display: "flex", gap: 20, marginBottom: 18 }}>
              <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
                <label style={{ marginBottom: 6 }}>Cân nặng <span style={{ color: "red" }}>*</span></label>
                <input style={{ width: "100%", padding: 8, borderRadius: 5, border: "1px solid #ccc", fontSize: 16 }} defaultValue="1" />
              </div>
              <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
                <label style={{ marginBottom: 6 }}>Chiều cao <span style={{ color: "red" }}>*</span></label>
                <input style={{ width: "100%", padding: 8, borderRadius: 5, border: "1px solid #ccc", fontSize: 16 }} defaultValue="1" />
              </div>
            </div>
            <div style={{ marginBottom: 18 }}>
              <label style={{ marginBottom: 6, display: "block" }}>Bị dị ứng với các loại nào</label>
              <textarea style={{ width: "100%", padding: 8, borderRadius: 5, border: "1px solid #ccc", fontSize: 16, minHeight: 40 }} defaultValue="gdf" />
            </div>
            <div style={{ marginBottom: 18 }}>
              <label style={{ marginBottom: 6, display: "block" }}>Bệnh mãn tính</label>
              <textarea style={{ width: "100%", padding: 8, borderRadius: 5, border: "1px solid #ccc", fontSize: 16, minHeight: 40 }} defaultValue="gdg" />
            </div>
            <div style={{ marginBottom: 18 }}>
              <label style={{ marginBottom: 6, display: "block" }}>Lịch sử điều trị</label>
              <textarea style={{ width: "100%", padding: 8, borderRadius: 5, border: "1px solid #ccc", fontSize: 16, minHeight: 40 }} defaultValue="dfgdf" />
            </div>
            <div>
              <label style={{ marginBottom: 6, display: "block" }}>Ghi chú</label>
              <textarea style={{ width: "100%", padding: 8, borderRadius: 5, border: "1px solid #ccc", fontSize: 16, minHeight: 40 }} defaultValue="gdfg" />
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
            {mockVaccines.map((v, idx) => (
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
                  value={v.name}
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
                  value={v.desc}
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