import React, { useEffect, useState } from "react";
import { List, Spin, message, Input, Avatar } from "antd";
import axios from "axios";
import './StudentList.css';
import { urlServer } from "../../../api/urlServer";
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
        const res = await axios.get(`${urlServer}/api/admin/students/${classId}`, {
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
    <div className="studentlist-container">
      <div className="studentlist-header">Danh sách học sinh</div>
      <div className="studentlist-search">
        <Input
          placeholder="Tìm học sinh..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          allowClear
          size="small"
        />
      </div>
      <div className="studentlist-scroll">
        {loading ? (
          <Spin style={{ margin: "32px auto", display: "block" }} />
        ) : (
          <List
            dataSource={filteredStudents}
            renderItem={(item) => (
              <List.Item
                className="studentlist-item"
                onClick={() => onSelectStudent && onSelectStudent(item)}
              >
                <Avatar style={{ backgroundColor: "#87d068", marginRight: 12 }}>
                  {item.fullName?.charAt(0).toUpperCase()}
                </Avatar>
                <span className="studentlist-name">{item.fullName}</span>
              </List.Item>
            )}
          />
        )}
      </div>
    </div>
  );
}
