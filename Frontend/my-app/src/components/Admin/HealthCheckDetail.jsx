// export default function HealthCheckDetail({ check }) {
//   if (!check) {
//     return <div style={{ padding: 20 }}>Chọn một học sinh để xem kết quả khám.</div>;
//   }

//   return (
//     <div className="student-detail" style={{ flex: 1, padding: "20px" }}>
//       {/* Dữ liệu sẽ được truyền từ API vào đây */}
//     </div>
//   );
// }

// HealthCheckDetail.jsx
import React from "react";

const studentResults = [
  { name: "Nguyễn Văn An", height: "140 cm", weight: "35 kg", vision: "6/10", pressure: "110/70", result: "Bình thường" },
  { name: "Trần Thị Bình", height: "138 cm", weight: "32 kg", vision: "8/10", pressure: "105/65", result: "Bình thường" },
  { name: "Lê Văn Cường", height: "142 cm", weight: "40 kg", vision: "7/10", pressure: "115/75", result: "Thừa cân" },
  { name: "Phạm Thị Dũng", height: "137 cm", weight: "30 kg", vision: "9/10", pressure: "100/60", result: "Bình thường" },
];

const HealthCheckDetail = ({ onClose }) => {
  return (
    <div style={popupStyle}>
      <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 12 }}>
        <strong>Chi Tiết Kiểm Tra</strong>
        <button onClick={onClose} style={{ fontSize: 16, background: "none", border: "none", cursor: "pointer" }}>✖</button>
      </div>

      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead style={{ backgroundColor: "#f5f5f5" }}>
          <tr>
            <th style={thStyle}>Học sinh</th>
            <th style={thStyle}>Chiều cao</th>
            <th style={thStyle}>Cân nặng</th>
            <th style={thStyle}>Thị lực</th>
            <th style={thStyle}>Huyết áp</th>
            <th style={thStyle}>Kết luận</th>
          </tr>
        </thead>
        <tbody>
          {studentResults.map((s, i) => (
            <tr key={i}>
              <td style={tdStyle}>{s.name}</td>
              <td style={tdStyle}>{s.height}</td>
              <td style={tdStyle}>{s.weight}</td>
              <td style={tdStyle}>{s.vision}</td>
              <td style={tdStyle}>{s.pressure}</td>
              <td style={tdStyle}>{s.result}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

const popupStyle = {
  backgroundColor: "#fff",
  borderRadius: "12px",
  padding: "20px",
  boxShadow: "0 8px 24px rgba(0,0,0,0.1)",
  width: "90%",
  maxWidth: "700px",
  margin: "20px auto",
  zIndex: 100,
};

const thStyle = { padding: "10px", textAlign: "left" };
const tdStyle = { padding: "10px", borderTop: "1px solid #eee" };

export default HealthCheckDetail;
