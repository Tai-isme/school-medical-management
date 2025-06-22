import React, { useEffect, useState } from "react";
import axios from "axios";

export default function MedicalHistory({ studentId }) {
  const [events, setEvents] = useState([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    if (!studentId) {
      setEvents([]);
      return;
    }
    setLoading(true);
    const fetchEvents = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get(
          `http://localhost:8080/api/nurse/medical-events/${studentId}`,
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );
        setEvents(Array.isArray(res.data) ? res.data : []);
      } catch (err) {
        setEvents([]);
      } finally {
        setLoading(false);
      }
    };
    fetchEvents();
  }, [studentId]);

  if (loading) return <div>Đang tải...</div>;

  return (
    <div style={{ background: "#fff", borderRadius: 8, padding: 16, minHeight: 100 }}>
      <div style={{ fontWeight: "bold", fontSize: 16, marginBottom: 8 }}>Theo dõi bệnh, sự cố y tế</div>
      {events.length === 0 && <div>Không có dữ liệu</div>}
      {events.map((e, idx) => (
        <div key={e.eventId || idx} style={{ marginBottom: 12, borderBottom: "1px solid #eee", paddingBottom: 8 }}>
          <div style={{ fontWeight: "bold", color: "#ff9800" }}>⚡ {e.typeEvent}</div>
          <div>Ngày xảy ra: {e.date}</div>
          <div>Mô tả: {e.description}</div>
          <div>Học sinh: {e.studentName}</div>
          <div>Y tá phụ trách: {e.nurseId}</div>
        </div>
      ))}
    </div>
  );
}