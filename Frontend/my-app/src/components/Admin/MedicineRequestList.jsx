import React, { useEffect, useState } from "react";

const STATUS_MAP = {
  pending: { label: "Đang chờ", color: "#e0e0e0", text: "#333" },
  approved: { label: "Đã duyệt", color: "#d1fae5", text: "#059669" },
  rejected: { label: "Từ chối", color: "#fee2e2", text: "#b91c1c" },
};

export default function MedicineRequestList() {
  const [requests, setRequests] = useState([]);
  const [search, setSearch] = useState("");
  const [date, setDate] = useState("");
  const [status, setStatus] = useState("");
  const [filtered, setFiltered] = useState([]);
  const [selectedRequest, setSelectedRequest] = useState(null); // ✅ Thêm để hiện modal

  // Hàm định dạng ngày từ "2025-06-15" sang "15/06/2025"
  const formatDate = (isoDate) => {
    if (!isoDate) return "";
    const [year, month, day] = isoDate.split("-");
    return `${day}/${month}/${year}`;
  };

  useEffect(() => {
    const fetchData = async () => {
      try {
        const token = localStorage.getItem("token");
        const response = await fetch("http://localhost:8080/api/nurse/medical-request", {
          method: "GET",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
        });

        if (!response.ok) throw new Error("Failed to fetch");

        const data = await response.json();
        setRequests(data);
      } catch (error) {
        console.error("Lỗi khi gọi API:", error);
      }
    };

    fetchData();
  }, []);

  useEffect(() => {
    let data = requests;
    if (search)
      data = data.filter(
        (r) =>
          r.requestId?.toLowerCase().includes(search.toLowerCase()) ||
          r.student?.toLowerCase().includes(search.toLowerCase()) ||
          r.class?.toLowerCase().includes(search.toLowerCase())
      );

    if (date) {
      data = data.filter((r) => r.date === date); // dữ liệu từ API đã là yyyy-mm-dd
    }

    if (status) data = data.filter((r) => r.status === status);
    setFiltered(data);
  }, [search, date, status, requests]);

  return (
    <div style={{ background: "#fff", borderRadius: 8, padding: 24, margin: 24 }}>
      <h2 style={{ fontWeight: 700, fontSize: 32, marginBottom: 24 }}>
        Quản Lý Đơn Thuốc Gửi Từ Phụ Huynh
      </h2>

      {/* Bộ lọc */}
      <div style={{ display: "flex", gap: 12, marginBottom: 16 }}>
        <input
          placeholder="Tìm kiếm"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          style={{ flex: 1, padding: 8, borderRadius: 4, border: "1px solid #ddd" }}
        />
        <input
          type="date"
          value={date}
          onChange={(e) => setDate(e.target.value)}
          style={{ width: 160, padding: 8, borderRadius: 4, border: "1px solid #ddd" }}
        />
        <select
          value={status}
          onChange={(e) => setStatus(e.target.value)}
          style={{ width: 120, padding: 8, borderRadius: 4, border: "1px solid #ddd" }}
        >
          <option value="">Trạng thái</option>
          <option value="pending">Đang chờ</option>
          <option value="approved">Đã duyệt</option>
          <option value="rejected">Từ chối</option>
        </select>
        <button
          style={{
            background: "#1976d2",
            color: "#fff",
            border: "none",
            borderRadius: 4,
            padding: "8px 20px",
            fontWeight: 600,
            cursor: "pointer",
          }}
          onClick={() => {}}
        >
          Tìm kiếm
        </button>
      </div>

      {/* Bảng dữ liệu */}
      <table style={{ width: "100%", borderCollapse: "collapse", background: "#fff" }}>
        <thead>
          <tr style={{ background: "#f9fafb" }}>
            <th style={thStyle}>ID Đơn Thuốc</th>
            <th style={thStyle}>Học Sinh Nhận</th>
            <th style={thStyle}>Lớp</th>
            <th style={thStyle}>Ngày Gửi</th>
            <th style={thStyle}>Trạng Thái</th>
            <th style={thStyle}>Chi Tiết</th>
          </tr>
        </thead>
        <tbody>
          {filtered.map((r) => (
            <tr key={r.requestId} style={{ borderBottom: "1px solid #eee" }}>
              <td style={tdStyle}>{r.requestId}</td>
              <td style={tdStyle}>
                {r.student}
                <div style={{ color: "#888", fontSize: 13 }}>Lớp {r.class}</div>
              </td>
              <td style={tdStyle}>{r.class}</td>
              <td style={tdStyle}>{formatDate(r.date)}</td>
              <td style={tdStyle}>
                <span
                  style={{
                    background: STATUS_MAP[r.status]?.color || "#eee",
                    color: STATUS_MAP[r.status]?.text || "#333",
                    padding: "2px 10px",
                    borderRadius: 6,
                    fontWeight: 500,
                    fontSize: 14,
                  }}
                >
                  {STATUS_MAP[r.status]?.label || r.status}
                </span>
              </td>
              <td style={tdStyle}>
                <a
                  href="#"
                  onClick={() => setSelectedRequest(r)}
                  style={{ color: "#1976d2", fontWeight: 500 }}
                >
                  Xem
                </a>
              </td>
            </tr>
          ))}
          {filtered.length === 0 && (
            <tr>
              <td colSpan={6} style={{ textAlign: "center", color: "#888", padding: 24 }}>
                Không có dữ liệu
              </td>
            </tr>
          )}
        </tbody>
      </table>

      {/* Modal hiển thị chi tiết đơn thuốc */}
      {selectedRequest && (
        <div
          style={{
            position: "fixed",
            top: 0,
            left: 0,
            right: 0,
            bottom: 0,
            background: "rgba(0, 0, 0, 0.5)",
            display: "flex",
            justifyContent: "center",
            alignItems: "center",
            zIndex: 999,
          }}
          onClick={() => setSelectedRequest(null)}
        >
          <div
            style={{
              background: "#fff",
              padding: 24,
              borderRadius: 8,
              width: 400,
              boxShadow: "0 4px 12px rgba(0,0,0,0.2)",
            }}
            onClick={(e) => e.stopPropagation()}
          >
            <h3 style={{ marginBottom: 16 }}>Chi Tiết Đơn Thuốc</h3>
            <p><strong>ID:</strong> {selectedRequest.requestId}</p>
            <p><strong>Học sinh:</strong> {selectedRequest.student}</p>
            <p><strong>Lớp:</strong> {selectedRequest.class}</p>
            <p><strong>Ngày gửi:</strong> {formatDate(selectedRequest.date)}</p>
            <p><strong>Trạng thái:</strong> {STATUS_MAP[selectedRequest.status]?.label}</p>
            <p><strong>Tên thuốc:</strong> {selectedRequest.medicineName || "Chưa có"}</p>
            <p><strong>Liều dùng:</strong> {selectedRequest.dosage || "Chưa có"}</p>
            <p><strong>Ghi chú:</strong> {selectedRequest.note || "Không có"}</p>

            <button
              onClick={() => setSelectedRequest(null)}
              style={{
                marginTop: 20,
                background: "#1976d2",
                color: "#fff",
                padding: "8px 16px",
                borderRadius: 4,
                border: "none",
                cursor: "pointer",
              }}
            >
              Đóng
            </button>
          </div>
        </div>
      )}
    </div>
  );
}

const thStyle = {
  padding: "10px 8px",
  textAlign: "left",
  fontWeight: 600,
  fontSize: 15,
  borderBottom: "2px solid #eee",
};

const tdStyle = {
  padding: "10px 8px",
  fontSize: 15,
};
