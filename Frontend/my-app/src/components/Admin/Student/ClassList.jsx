import React, { useEffect, useState } from "react";
import { Input, List, Spin, message } from "antd";
import axios from "axios";
import classNames from "classnames";
import './ClassList.css'; 
import { urlServer } from "../../../api/urlServer";
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
        const res = await axios.get(`${urlServer}/api/admin/class`, {
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
    <div className="classlist-container">
      <div className="classlist-header">Danh sách lớp</div>
      <div className="classlist-search">
        <Input
          placeholder="Tìm lớp..."
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          allowClear
          size="small"
        />
      </div>
      <div className="classlist-scroll">
        {loading ? (
          <Spin style={{ margin: "32px auto", display: "block" }} />
        ) : (
          <List
            dataSource={filteredClasses}
            renderItem={(item, idx) => (
              <List.Item
                className={classNames("classlist-item", {
                  "classlist-item-hovered": hoveredIndex === idx,
                  "classlist-item-selected": selectedIndex === idx,
                })}
                onMouseEnter={() => setHoveredIndex(idx)}
                onMouseLeave={() => setHoveredIndex(null)}
                onClick={() => {
                  setSelectedIndex(idx);
                  onSelectClass && onSelectClass(item);
                }}
              >
                Lớp: {item.className}
                <br />
                Giáo viên: {item.teacherName || "Chưa có giáo viên"}
              </List.Item>
            )}
          />
        )}
      </div>
    </div>
  );
}
