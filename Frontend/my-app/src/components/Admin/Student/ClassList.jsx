import React, { useEffect, useState } from "react";
import { Input, List, Spin, message } from "antd";
import axios from "axios";

export default function ClassList({ onSelectClass }) {
  const [search, setSearch] = useState("");
  const [classData, setClassData] = useState([]);
  const [loading, setLoading] = useState(false);
  const [hoveredIndex, setHoveredIndex] = useState(null);
  const [selectedIndex, setSelectedIndex] = useState(null);

  useEffect(() => {
    const fetchClasses = async () => {
      setLoading(true);
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get("http://localhost:8080/api/admin/class", {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setClassData(res.data);
      } catch (err) {
        message.error("Không thể tải danh sách lớp!");
      }
      setLoading(false);
    };
    fetchClasses();
  }, []);

  const filteredClasses = classData.filter((cls) =>
    cls.className.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div
      style={{
        width: 260,
        height: "100vh",
        background: "#fff",
        borderRight: "1px solid #e5e5e5",
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
      }}
    >
      <div
        style={{
          fontWeight: 600,
          fontSize: 16,
          padding: "16px 0 8px 0",
          borderBottom: "1px solid #eee",
          background: "#fff",
          width: "100%",
          textAlign: "center",
        }}
      >
        Danh sách lớp
      </div>
      <div style={{ padding: "12px 0 8px 0", width: "90%" }}>
        <Input
          placeholder="Tìm lớp..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          allowClear
          size="small"
        />
      </div>
      <div style={{ flex: 1, overflowY: "auto", width: "100%" }}>
        {loading ? (
          <Spin style={{ margin: "32px auto", display: "block" }} />
        ) : (
          <List
            dataSource={filteredClasses}
            renderItem={(item, idx) => (
              <List.Item
                style={{
                  cursor: "pointer",
                  padding: "8px 0",
                  border: "none",
                  borderRadius: 4,
                  margin: "0 auto 4px auto",
                  background:
                    selectedIndex === idx
                      ? "#3399ff"
                      : hoveredIndex === idx
                      ? "#e6f7ff"
                      : "#fff",
                  transition: "background 0.2s",
                  width: "80%",
                  justifyContent: "center",
                  textAlign: "left",
                  boxShadow:
                    hoveredIndex === idx || selectedIndex === idx
                      ? "0 2px 8px #91d5ff55"
                      : "none",
                  fontWeight: selectedIndex === idx ? 600 : 400,
                }}
                onMouseEnter={() => setHoveredIndex(idx)}
                onMouseLeave={() => setHoveredIndex(null)}
                onClick={() => {
                  setSelectedIndex(idx);
                  onSelectClass && onSelectClass(item);
                }}
              >
                Lớp: {item.className}
                <br></br>
                Giáo viên: {item.teacherName || "Chưa có giáo viên"}
              </List.Item>
            )}
          />
        )}
      </div>
    </div>
  );
}