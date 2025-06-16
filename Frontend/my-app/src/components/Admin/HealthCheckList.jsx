// export default function HealthCheckList({ students, onSelect }) {
//   return (
//     <div className="student-list">
//       <h3>Danh sách khám định kỳ</h3>
//       {/* Dữ liệu sẽ được truyền từ API vào đây */}
//       {students && students.length > 0 && students.map((student, index) => (
//         <div
//           key={index}
//           onClick={() => onSelect(student)}
//           className="student-item"
//         >
//           <strong>{student.name}</strong>
//           <div style={{ fontSize: '12px', color: '#666' }}>
//             {student.className}
//           </div>
//         </div>
//       ))}
//     </div>
//   );
// }

// HealthCheckList.jsx
import React from "react";

const checkupData = [
  { date: "24 April 2024", className: "5A", totalStudents: 30, status: "Chưa khám" },
  { date: "15 March 2024", className: "4B", totalStudents: 28, status: "Đã khám" },
  { date: "10 February 2024", className: "2C", totalStudents: 26, status: "Chưa khám" },
];

const HealthCheckList = ({ onSelectCheckup }) => {
  return (
    <div style={{ padding: "24px" }}>
      <div style={{ display: "flex", justifyContent: "space-between", marginBottom: 16 }}>
        <h2>Khám Sức Khỏe Định Kỳ</h2>
        <button style={{
          padding: "8px 12px",
          backgroundColor: "#1976d2",
          color: "white",
          border: "none",
          borderRadius: "6px",
          cursor: "pointer"
        }}>
          + Thêm Kiểm Tra Sức Khỏe
        </button>
      </div>

      <table style={{ width: "100%", borderCollapse: "collapse", background: "white", borderRadius: "8px", overflow: "hidden" }}>
        <thead style={{ backgroundColor: "#f5f5f5" }}>
          <tr>
            <th style={thStyle}>Ngày</th>
            <th style={thStyle}>Lớp</th>
            <th style={thStyle}>Số lượng học sinh</th>
            <th style={thStyle}>Trạng thái</th>
            <th style={thStyle}>Hành động</th>
          </tr>
        </thead>
        <tbody>
          {checkupData.map((row, idx) => (
            <tr key={idx}>
              <td style={tdStyle}>{row.date}</td>
              <td style={tdStyle}>{row.className}</td>
              <td style={tdStyle}>{row.totalStudents}</td>
              <td style={tdStyle}>
                <span style={{
                  backgroundColor: row.status === "Đã khám" ? "#d0f0da" : "#eee",
                  color: row.status === "Đã khám" ? "#1b5e20" : "#555",
                  padding: "2px 10px",
                  borderRadius: "12px",
                  fontSize: "14px"
                }}>{row.status}</span>
              </td>
              <td style={tdStyle}>
                <button onClick={() => onSelectCheckup(row)} style={{ color: "#1976d2", border: "none", background: "none", cursor: "pointer" }}>
                  Chi tiết
                </button>
              </td>
            </tr>
          ))}
        </tbody>
      </table>
    </div>
  );
};

const thStyle = { textAlign: "left", padding: "12px", fontWeight: 600 };
const tdStyle = { padding: "12px", borderTop: "1px solid #eee" };

export default HealthCheckList;
