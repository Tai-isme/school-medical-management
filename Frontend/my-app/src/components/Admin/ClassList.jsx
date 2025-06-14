import React, { useEffect, useRef, useState } from "react";

export default function ClassList({ onSelectClass, selectedClass }) {
  const [classList, setClassList] = useState([]);
  const [search, setSearch] = useState("");
  const refs = useRef({});

  useEffect(() => {
    const token = localStorage.getItem("token");
    fetch("http://localhost:8080/api/admin/class", {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then((res) => res.json())
      .then((data) => setClassList(data))
      .catch((err) => console.error("Lỗi lấy danh sách lớp:", err));
  }, []);

  useEffect(() => {
    const targetClass = classList.find((cls) => cls.classId === selectedClass);
    if (targetClass) {
      const targetRef = refs.current[targetClass.className];
      if (targetRef) {
        targetRef.scrollIntoView({ behavior: "smooth", block: "center" });
      }
    }
  }, [classList, selectedClass]);

  const filteredClassList = classList.filter((cls) =>
    cls.className.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div
      style={{
        width: "100%",
        display: "flex",
        flexDirection: "column",
        alignItems: "flex-start",
      }}
    >
      <h4 style={{ marginBottom: 8 }}>Danh sách lớp</h4>
      <input
        type="text"
        placeholder="Tìm lớp..."
        value={search}
        onChange={(e) => setSearch(e.target.value)}
        style={{
          marginBottom: 12,
          width: "100%",
          padding: "6px 8px",
          fontSize: 14,
          borderRadius: 4,
          border: "1px solid #ccc",
        }}
      />

      {/* VÙNG CUỘN CHỈ HIỆN 3 LỚP */}
      <div
        style={{
          width: "100%",
          overflowY: "auto",
          maxHeight: "130px", // chỉ đủ khoảng 3 lớp
        }}
      >
        {filteredClassList.map((cls) => (
          <div
            key={cls.classId}
            ref={(el) => {
              refs.current[cls.className] = el;
            }}
            onClick={() => onSelectClass && onSelectClass(cls.classId)}
            style={{
              backgroundColor:
                selectedClass === cls.classId ? "#e0f7fa" : "transparent",
              cursor: "pointer",
              fontWeight: selectedClass === cls.classId ? "bold" : "normal",
              marginBottom: 6,
              fontSize: 15,
              width: "100%",
              textAlign: "center",
              borderRadius: 4,
              padding: "6px 0",
              height: 36, // cố định chiều cao mỗi lớp
              boxSizing: "border-box",
            }}
          >
            {cls.className}
          </div>
        ))}
        {filteredClassList.length === 0 && (
          <p style={{ fontSize: 12 }}>Không tìm thấy lớp.</p>
        )}
      </div>
    </div>
  );
}
