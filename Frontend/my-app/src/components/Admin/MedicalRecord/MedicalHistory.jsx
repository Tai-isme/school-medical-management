import React from "react";

const mockEvents = [
  {
    type: "Trượt chân",
    category: "Ngã",
    date: "07/10/2020",
    solution: "Sơ ý",
  },
  // Thêm các sự kiện khác nếu muốn
];

export default function MedicalHistory({ events = mockEvents }) {
  return (
    <div style={{ background: "#fff", borderRadius: 8, padding: 16, minHeight: 100 }}>
      <div style={{ fontWeight: "bold", fontSize: 16, marginBottom: 8 }}>Theo dõi bệnh, sự cố y tế</div>
      {events.map((e, idx) => (
        <div key={idx} style={{ marginBottom: 12, borderBottom: "1px solid #eee", paddingBottom: 8 }}>
          <div style={{ fontWeight: "bold", color: "#ff9800" }}>⚡ {e.type}</div>
          <div>Loại sự cố: {e.category}</div>
          <div>Ngày xảy ra sự cố: {e.date}</div>
          <div>Biện pháp xử lý: {e.solution}</div>
        </div>
      ))}
    </div>
  );
}