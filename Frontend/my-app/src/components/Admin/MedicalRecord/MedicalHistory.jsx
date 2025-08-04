import React, { useEffect, useState } from "react";
import axios from "axios";
import dayjs from "dayjs";

import { urlServer } from "../../../api/urlServer";

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
          `${urlServer}/api/nurse/medical-events/${studentId}`,
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
          marginBottom: 8,
        }}
      >
        Theo dõi bệnh, sự cố y tế
      </div>
      {events.length === 0 && <div>Không có dữ liệu</div>}
      {events.map((e, idx) => (
        <div
          key={e.eventId || idx}
          style={{
            marginBottom: 12,
            borderBottom: "1px solid #eee",
            paddingBottom: 8,
          }}
        >
          <div
            style={{
              fontWeight: "bold",
              color: "#d48806",
              backgroundColor: "#fff7e6",
              padding: "6px 12px",
              borderRadius: 8,
              display: "inline-block",
              marginBottom: 6,
            }}
          >
            ⚡Sự cố: {e.typeEvent}
          </div>
          <div>
            <b>Ngày xảy ra:</b> {dayjs(e.date).format("DD/MM/YYYY")}
          </div>
          <div>
            <b>Xử lý:</b> {e.actionsTaken}
          </div>
          <div>
            <b>Địa điểm:</b> {e.location}
          </div>
          <div>
            <b>Y tá phụ trách:</b> {e.nurseDTO.fullName}
          </div>
          <div>
            <b>Mô tả:</b> {e.description}
          </div>
        </div>
      ))}
    </div>
  );
}
