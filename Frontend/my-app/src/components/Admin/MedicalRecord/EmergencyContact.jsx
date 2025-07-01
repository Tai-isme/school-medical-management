import React from "react";

const mockContact = {
  name: "Trần Thị Huyền My (mẹ)",
  phone: "0346426863",
  address: "số 22, xóm 6/thôn 19, Xã Vũ Ninh, H...",
};

export default function EmergencyContact({ parentInfo }) {
  if (!parentInfo) return <div>Không có thông tin phụ huynh</div>;
  return (
    <div
      style={{
        background: "#fff",
        borderRadius: 16,
        padding: 20,
        minHeight: 100,
        fontFamily: "Segoe UI, Arial, sans-serif",
      }}
    >
      <div
        style={{
          fontWeight: "bold",
          fontSize: 16,
          marginBottom: 4,
        }}
      >
        Liên hệ khẩn cấp
      </div>
      <div style={{ fontWeight: "bold", color: "#009688" }}>
        {parentInfo.fullName}
      </div>
      <div>📞 {parentInfo.phone}</div>
      <div>🏠 {parentInfo.address}</div>
    </div>
  );
}