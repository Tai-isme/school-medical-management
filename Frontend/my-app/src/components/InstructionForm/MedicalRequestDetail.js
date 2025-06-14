import React from "react";

export default function MedicalRequestDetail({ history}) {
  return (
    <div>
      <table style={{ width: "100%", borderCollapse: "collapse" }}>
        <thead>
          <tr style={{ background: "#2196f3", color: "#fff" }}>
            <th style={{ padding: 10, border: "1px solid #222" }}>Mã đơn thuốc</th>
            <th style={{ padding: 10, border: "1px solid #222" }}>ID Học sinh nhận thuốc</th>
            <th style={{ padding: 10, border: "1px solid #222" }}>Ngày gửi</th>
            <th style={{ padding: 10, border: "1px solid #222" }}>Trạng thái</th>
            <th style={{ padding: 10, border: "1px solid #222" }}>Xem chi tiết đơn thuốc</th>
          </tr>
        </thead>
        <tbody>
          {history.length === 0 ? (
            <tr>
              <td colSpan={5} style={{ textAlign: "center", padding: 20 }}>
                Chưa có đơn thuốc nào được gửi.
              </td>
            </tr>
          ) : (
            history.map((req, idx) => (
              <tr key={idx}>
                <td style={{ border: "1px solid #222", textAlign: "center" }}>{req.id || "1521"}</td>
                <td style={{ border: "1px solid #222", textAlign: "center" }}>{req.studentId || ""}</td>
                <td style={{ border: "1px solid #222", textAlign: "center" }}>{req.date || ""}</td>
                <td style={{ border: "1px solid #222", textAlign: "center" }}>{req.status || "Chờ xác nhận"}</td>
                <td style={{ border: "1px solid #222", textAlign: "center", color: "#1976d2", cursor: "pointer" }}
                  
                >
                  Nhấn để xem
                </td>
              </tr>
            ))
          )}
        </tbody>
      </table>
    </div>
  );
}