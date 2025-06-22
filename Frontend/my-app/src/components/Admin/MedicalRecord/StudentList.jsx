import React, { useState, useEffect } from "react";
import axios from "axios";

export default function StudentList({ onSelect, selectedId, onFirstStudentLoaded }) {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(true);
  const [nameFilter, setNameFilter] = useState("");
  const [classFilter, setClassFilter] = useState("");

  useEffect(() => {
    const fetchStudents = async () => {
      try {
        const token = localStorage.getItem("token"); // hoặc nơi bạn lưu token
        const res = await axios.get(
          `http://localhost:8080/api/nurse/students`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setStudents(res.data);
        if (onFirstStudentLoaded && res.data.length > 0) {
          onFirstStudentLoaded(res.data[0]);
        }
        console.log("Fetched students:", res.data);
      } catch (err) {
        setStudents([]);
      } finally {
        setLoading(false);
      }
    };
    fetchStudents();
  }, []);

  const filteredStudents = students.filter(
    (s) =>
      s.fullName?.toLowerCase().includes(nameFilter.toLowerCase()) &&
      (s.classDTO?.className || "").toLowerCase().includes(classFilter.toLowerCase())
  );

  if (loading) return <div>Đang tải...</div>;

  return (
    <div style={{ background: "#f7fbff", paddingLeft: "10px" , width: "420px" , height: "100%", overflowY: "auto"}}>
      <div style={{ display: "flex", fontWeight: "bold", marginBottom: 0, gap: 12 }}>
        <div style={{ flex: 2, display: "flex", flexDirection: "column" }}>
          <span>Họ tên</span>
          <input
            style={{ width: "100%", padding: 4, borderRadius: 4, border: "1px solid #ccc", marginTop: 2, marginBottom: 8 }}
            placeholder="Lọc tên..."
            value={nameFilter}
            onChange={e => setNameFilter(e.target.value)}
          />
        </div>
        <div style={{ flex: 1, display: "flex", flexDirection: "column" }}>
          <span>Lớp</span>
          <input
            style={{ width: "90%", padding: 4, borderRadius: 4, border: "1px solid #ccc", marginTop: 2, marginBottom: 8 }}
            placeholder="Lọc lớp..."
            value={classFilter}
            onChange={e => setClassFilter(e.target.value)}
          />
        </div>
      </div>
      {filteredStudents.map((s) => (
        <div
          key={s.id}
          onClick={() => onSelect(s)}
          style={{
            display: "flex",
            padding: "8px 12px",
            background: selectedId === s.id ? "#e6f7ff" : "#fff",
            cursor: "pointer",
            borderBottom: "1px solid #eee",
            fontWeight: selectedId === s.id ? "bold" : "normal",
          }}
        >
          <div style={{ flex: 2, borderRight: "1px solid #bdbdbd", paddingRight: 50 }}>
            {s.fullName}
          </div>
          <div style={{ flex: 1, color: "#888", paddingLeft: 8 }}>
            {s.classDTO?.className}
          </div>
        </div>
      ))}
      <div style={{ marginTop: 8, color: "#888" }}>Tổng số: {filteredStudents.length} học sinh</div>
    </div>
  );
}