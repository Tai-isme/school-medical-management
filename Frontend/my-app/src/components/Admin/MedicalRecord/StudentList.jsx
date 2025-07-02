import React, { useState, useEffect } from "react";
import axios from "axios";

export default function StudentList({ onSelect, selectedId, onFirstStudentLoaded }) {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [nameFilter, setNameFilter] = useState("");
  const [selectedClass, setSelectedClass] = useState(""); // className
  const [classList, setClassList] = useState([]);

  useEffect(() => {
    const fetchStudents = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get(
          `http://localhost:8080/api/nurse/students`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setStudents(res.data);
        // Lấy danh sách lớp duy nhất
        const classes = Array.from(
          new Set(res.data.map(s => s.classDTO?.className).filter(Boolean))
        );
        setClassList(classes);
        if (onFirstStudentLoaded && res.data.length > 0) {
          onFirstStudentLoaded(res.data[0]);
        }
      } catch (err) {
        setStudents([]);
      } finally {
        setLoading(false);
      }
    };
    fetchStudents();
  }, []);

  // Lọc học sinh theo lớp đã chọn và tên
  const filteredStudents = students.filter(
    (s) =>
      (selectedClass === "" || s.classDTO?.className === selectedClass) &&
      s.fullName?.toLowerCase().includes(nameFilter.toLowerCase())
  );

  if (loading) return <div>Đang tải...</div>;

  return (
    <div style={{
      display: "flex",
      width: "100%",
      height: "910px",
      background: "#f7fbff",
      borderRadius: 16,
      fontFamily: "Segoe UI, Arial, sans-serif"
    }}>
      {/* Cột lớp */}
      <div style={{
        minWidth: 110,
        maxWidth: 140,
        borderRight: "1px solid #e0e0e0",
        padding: "16px 0 8px 0",
        background: "#f7fbff"
      }}>
        <div style={{ fontWeight: "bold", marginBottom: 8, textAlign: "center" }}>Lớp</div>
        <div>
          {classList.map((cls) => (
            <div
              key={cls}
              onClick={() => setSelectedClass(cls)}
              style={{
                cursor: "pointer",
                padding: "8px 12px",
                background: selectedClass === cls ? "#e3f2fd" : "transparent",
                fontWeight: selectedClass === cls ? "bold" : "normal",
                borderRadius: 6,
                marginBottom: 4,
                textAlign: "center"
              }}
            >
              {cls}
            </div>
          ))}
          <div
            onClick={() => setSelectedClass("")}
            style={{
              cursor: "pointer",
              padding: "8px 12px",
              background: selectedClass === "" ? "#e3f2fd" : "transparent",
              fontWeight: selectedClass === "" ? "bold" : "normal",
              borderRadius: 6,
              marginTop: 12,
              textAlign: "center",
              color: "#1976d2"
            }}
          >
            Tất cả
          </div>
        </div>
      </div>
      {/* Cột học sinh */}
      <div style={{
        flex: 1,
        padding: "0 10px",
        overflowY: "auto"
      }}>
        <div style={{
          position: "sticky",
          top: 0,
          zIndex: 2,
          background: "#f7fbff",
          padding: "16px 0 8px 0",
        }}>
          <div style={{ display: "flex", fontWeight: "bold", gap: 12 }}>
            <span style={{ flex: 2 }}>Họ tên</span>
          </div>
          <div style={{ display: "flex", gap: 12, marginBottom: 8 }}>
            <input
              style={{ flex: 2, padding: 4, borderRadius: 4, border: "1px solid #ccc" }}
              placeholder="Lọc tên..."
              value={nameFilter}
              onChange={e => setNameFilter(e.target.value)}
            />
          </div>
        </div>
        {filteredStudents.map((s) => (
          <div
            key={s.id}
            onClick={() => onSelect(s)}
            style={{
              display: "flex",
              padding: "10px 16px",
              background: selectedId === s.id ? "#e3f2fd" : "#fff",
              cursor: "pointer",
              borderBottom: "1px solid #eee",
              fontWeight: selectedId === s.id ? "bold" : "normal",
              borderRadius: 8,
              marginBottom: 4,
              transition: "background 0.2s",
              boxShadow: selectedId === s.id ? "0 2px 8px #1976d220" : "none",
            }}
            onMouseEnter={e => e.currentTarget.style.background = "#f1f8ff"}
            onMouseLeave={e => e.currentTarget.style.background = selectedId === s.id ? "#e3f2fd" : "#fff"}
          >
            <div style={{ flex: 2, paddingLeft: 8 }}>
              {s.fullName}
            </div>
          </div>
        ))}
        <div style={{ marginTop: 8, color: "#888" }}>Tổng số: {filteredStudents.length} học sinh</div>
      </div>
    </div>
  );
}