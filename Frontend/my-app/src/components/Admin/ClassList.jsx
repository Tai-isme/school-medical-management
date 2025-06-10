import React, { useEffect, useState } from "react";

export default function ClassList({ onSelectClass, selectedClass }) {
  const [classList, setClassList] = useState([]);
  const [search, setSearch] = useState("");

  useEffect(() => {
    const token = localStorage.getItem("token");
    fetch("http://localhost:8080/api/admin/class", {
      headers: {
        Authorization: `Bearer ${token}`
      }
    })
      .then(res => res.json())
      .then(data => setClassList(data))
      .catch(err => console.error("Lỗi lấy danh sách lớp:", err));
  }, []);

  const filteredClassList = classList.filter(cls =>
    cls.className.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div
      style={{
        width: '200px',
        height: '100vh',
        padding: '10px',
        borderRight: '1px solid #ccc',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
      }}
    >
      <h4 style={{ marginBottom: 20 }}>Danh sách lớp</h4>
      <input
        type="text"
        placeholder="Tìm lớp..."
        value={search}
        onChange={e => setSearch(e.target.value)}
        style={{
          marginBottom: 16,
          width: "100%",
          padding: "6px 8px",
          fontSize: 15,
          borderRadius: 4,
          border: "1px solid #ccc"
        }}
      />
      {filteredClassList.map((cls) => (
        <div
          key={cls.classId}
          onClick={() => onSelectClass && onSelectClass(cls.classId)}
          style={{
            backgroundColor: selectedClass === cls.classId ? "#e0f7fa" : "transparent",
            cursor: 'pointer',
            fontWeight: selectedClass === cls.classId ? 'bold' : 'normal',
            marginBottom: 10,
            fontSize: 18,
            width: "100%",
            textAlign: "center",
            borderRadius: 6
          }}
        >
          {cls.className}
        </div>
      ))}
    </div>
  );
}
