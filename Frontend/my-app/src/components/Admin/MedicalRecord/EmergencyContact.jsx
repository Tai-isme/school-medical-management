import React from "react";

const mockContact = {
  name: "Trần Thị Huyền My (mẹ)",
  phone: "0346426863",
  address: "số 22, xóm 6/thôn 19, Xã Vũ Ninh, H...",
};

export default function EmergencyContact({ contact = mockContact }) {
  return (
    <div style={{ background: "#fff", borderRadius: 8, padding: 16, minHeight: 100 }}>
      <div style={{ fontWeight: "bold", fontSize: 16, marginBottom: 4 }}>Liên hệ khẩn cấp</div>
      <div style={{ fontWeight: "bold", color: "#009688" }}>{contact.name}</div>
      <div>📞 {contact.phone}</div>
      <div>🏠 {contact.address}</div>
    </div>
  );
}