import React, { useEffect, useState } from "react";
import { List, Spin, message, Input, Avatar } from "antd";
import axios from "axios";

export default function StudentList({ classId, onSelectStudent }) {
  const [students, setStudents] = useState([]);
  const [loading, setLoading] = useState(false);
  const [search, setSearch] = useState("");

  useEffect(() => {
    if (!classId) return;
    const fetchStudents = async () => {
      setLoading(true);
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get(`http://localhost:8080/api/admin/students/${classId}`, {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        });
        setStudents(res.data);
      } catch (err) {
        message.error("Không thể tải danh sách học sinh!");
      }
      setLoading(false);
    };
    fetchStudents();
  }, [classId]);

  const filteredStudents = students.filter((student) =>
    student.fullName?.toLowerCase().includes(search.toLowerCase())
  );

  return (
    <div
      style={{
        width: 270,
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
        Danh sách học sinh
      </div>
      <div style={{ padding: "12px 0 8px 0", width: "90%" }}>
        <Input
          placeholder="Tìm học sinh..."
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
            dataSource={filteredStudents}
            renderItem={(item) => (
              <List.Item
                style={{
                  cursor: "pointer",
                  padding: "8px 0",
                  border: "none",
                  borderRadius: 4,
                  margin: "0 auto 4px auto",
                  background: "#fff",
                  transition: "background 0.2s",
                  width: "85%",
                  justifyContent: "center",
                  textAlign: "center",
                  display: "flex",
                  alignItems: "center",
                }}
                onClick={() => onSelectStudent && onSelectStudent(item)}
              >
                <Avatar style={{ backgroundColor: "#87d068", marginRight: 12 }}>
                  {item.fullName?.charAt(0).toUpperCase()}
                </Avatar>
                <span style={{ flex: 1, textAlign: "left" }}>{item.fullName}</span>
              </List.Item>
            )}
          />
        )}
      </div>
    </div>
  );
}