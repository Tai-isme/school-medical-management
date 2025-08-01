import React, { useEffect, useState } from "react";
import axios from "axios";
import {
  Card,
  Modal,
  message,
  Pagination,
  Input,
  Select,
  Row,
  Col,
} from "antd";
import dayjs from "dayjs";

const { Search } = Input;
const { Option } = Select;

const MedicalEventList = () => {
  const [data, setData] = useState([]);
  const [filteredData, setFilteredData] = useState([]);
  const [selectedEvent, setSelectedEvent] = useState(null);
  const [detailModalVisible, setDetailModalVisible] = useState(false);
  const [currentPage, setCurrentPage] = useState(1);
  const [searchText, setSearchText] = useState("");
  const [selectedClass, setSelectedClass] = useState("");
  const [selectedLevel, setSelectedLevel] = useState("");

  const pageSize = 3;

  useEffect(() => {
    const fetchMedicalEvents = async () => {
      try {
        const token = localStorage.getItem("token");
        const res = await axios.get(
          "http://localhost:8080/api/nurse/medical-event",
          {
            headers: { Authorization: `Bearer ${token}` },
          }
        );

        const apiData = res.data.map((item) => ({
          ...item,
          studentName: item.studentDTO?.fullName || `ID ${item.studentId}`,
          className: item.classDTO?.className || "Không rõ",
          date: item.date,
          nurseName: item.nurseDTO?.fullName || `ID ${item.nurseId}`,
        }));

        setData(apiData);
        setFilteredData(apiData);
      } catch (error) {
        console.error("Lỗi tải sự kiện y tế:", error);
        message.error("Không thể tải dữ liệu sự kiện y tế!");
      }
    };

    fetchMedicalEvents();
  }, []);

  useEffect(() => {
    let tempData = [...data];

    if (searchText.trim() !== "") {
      tempData = tempData.filter(
        (item) =>
          item.eventName?.toLowerCase().includes(searchText.toLowerCase()) ||
          item.studentName?.toLowerCase().includes(searchText.toLowerCase())
      );
    }

    if (selectedClass) {
      tempData = tempData.filter((item) => item.className === selectedClass);
    }

    if (selectedLevel) {
      tempData = tempData.filter((item) => item.levelCheck === selectedLevel);
    }

    setFilteredData(tempData);
    setCurrentPage(1); // reset về trang 1 khi lọc hoặc tìm
  }, [searchText, selectedClass, selectedLevel, data]);

  const getUniqueClassNames = () => {
    return [...new Set(data.map((item) => item.className))];
  };

  const paginatedData = filteredData.slice(
    (currentPage - 1) * pageSize,
    currentPage * pageSize
  );

  const containerStyle = {
    marginLeft: "240px",
    flex: 1,
    padding: "24px",
    background: "#fafbfc",
    minHeight: "100vh",
    borderRadius: "10px",
    boxSizing: "border-box",
    boxShadow: "0 4px 12px rgba(0, 0, 0, 0.04)",
    fontFamily: "'Segoe UI', Tahoma, sans-serif",
  };

  const titleStyle = {
    fontSize: "24px",
    color: "#1476d1",
    marginBottom: "24px",
    fontWeight: 600,
  };

  const cardStyle = {
    marginBottom: 16,
    borderRadius: 10,
    boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
    cursor: "pointer",
    transition: "box-shadow 0.2s",
  };

  const cardHoverStyle = {
    ...cardStyle,
    boxShadow: "0 4px 16px rgba(24, 144, 255, 0.12)",
  };

  const eventTitleStyle = {
    color: "#ff4d4f",
    textTransform: "uppercase",
    fontWeight: "bold",
  };

  const getLevelStyle = (level) => {
    const baseStyle = {
      position: "absolute",
      top: 16,
      right: 16,
      padding: "4px 10px",
      borderRadius: 20,
      color: "#fff",
      fontWeight: "bold",
      fontSize: 12,
      textTransform: "uppercase",
    };

    switch (level) {
      case "LOW":
        return { ...baseStyle, backgroundColor: "#52c41a" };
      case "MEDIUM":
        return { ...baseStyle, backgroundColor: "#faad14" };
      case "HIGH":
        return { ...baseStyle, backgroundColor: "#ff4d4f" };
      default:
        return { ...baseStyle, backgroundColor: "#d9d9d9" };
    }
  };

  return (
    <div style={containerStyle}>
      <h2 style={titleStyle}>Danh sách sự kiện y tế</h2>

      {/* Tìm kiếm và lọc */}
      <Row gutter={16} style={{ marginBottom: 24 }}>
        <Col span={8}>
          <Search
            placeholder="Tìm theo tên sự kiện hoặc học sinh"
            onChange={(e) => setSearchText(e.target.value)}
            allowClear
          />
        </Col>
        <Col span={8}>
          <Select
            placeholder="Lọc theo lớp"
            style={{ width: "100%" }}
            onChange={(value) => setSelectedClass(value)}
            allowClear
          >
            {getUniqueClassNames().map((cls) => (
              <Option key={cls} value={cls}>
                {cls}
              </Option>
            ))}
          </Select>
        </Col>
        <Col span={8}>
          <Select
            placeholder="Lọc theo mức độ"
            style={{ width: "100%" }}
            onChange={(value) => setSelectedLevel(value)}
            allowClear
          >
            <Option value="LOW">LOW</Option>
            <Option value="MEDIUM">MEDIUM</Option>
            <Option value="HIGH">HIGH</Option>
          </Select>
        </Col>
      </Row>

      {/* Danh sách sự kiện */}
      {filteredData.length === 0 ? (
        <p>Không có dữ liệu phù hợp.</p>
      ) : (
        <>
          {paginatedData.map((item) => (
            <Card
              key={item.eventId}
              style={{ ...cardStyle, position: "relative" }}
              onMouseEnter={(e) => {
                e.currentTarget.style.boxShadow = cardHoverStyle.boxShadow;
              }}
              onMouseLeave={(e) => {
                e.currentTarget.style.boxShadow = cardStyle.boxShadow;
              }}
              onClick={() => {
                setSelectedEvent(item);
                setDetailModalVisible(true);
              }}
            >
              <div style={getLevelStyle(item.levelCheck)}>
                {item.levelCheck}
              </div>
              <h3 style={eventTitleStyle}>{item.eventName}</h3>
              <p>
                <strong>Học sinh:</strong> {item.studentName}
              </p>
              <p>
                <strong>Lớp:</strong> {item.className}
              </p>
              <p>
                <strong>Ngày:</strong> {dayjs(item.date).format("DD/MM/YYYY")}
              </p>
            </Card>
          ))}

          <Pagination
            current={currentPage}
            pageSize={pageSize}
            total={filteredData.length}
            onChange={(page) => setCurrentPage(page)}
            style={{ marginTop: 24, textAlign: "center" }}
          />
        </>
      )}

      {/* Modal chi tiết */}
      <Modal
        title={
          <span style={{ color: "#1476d1", fontSize: 20, fontWeight: 600 }}>
            Chi tiết sự kiện y tế
          </span>
        }
        open={detailModalVisible}
        onCancel={() => setDetailModalVisible(false)}
        footer={null}
        bodyStyle={{
          position: "relative",
          padding: 24,
          paddingTop: 40,
          backgroundColor: "#fefefe",
          borderRadius: 8,
        }}
      >
        {selectedEvent && (
          <div>
            {/* Mức độ hiển thị góc phải */}
            <div style={getLevelStyle(selectedEvent.levelCheck)}>
              {selectedEvent.levelCheck}
            </div>

            {/* Dạng lưới 2 cột */}
            <div
              style={{
                display: "grid",
                gridTemplateColumns: "1fr 1fr",
                gap: "12px 24px",
                lineHeight: "1.8em",
              }}
            >
              <div>
                <strong>Tên sự kiện:</strong>
                <div>{selectedEvent.eventName}</div>
              </div>

              <div>
                <strong>Loại sự kiện:</strong>
                <div>{selectedEvent.typeEvent}</div>
              </div>

              <div>
                <strong>Học sinh:</strong>
                <div>{selectedEvent.studentName}</div>
              </div>

              <div>
                <strong>Lớp:</strong>
                <div>{selectedEvent.className}</div>
              </div>

              <div>
                <strong>Y tá phụ trách 👩‍⚕️:</strong>
                <div>{selectedEvent.nurseName}</div>
              </div>

              <div>
                <strong>Địa điểm:</strong>
                <div>{selectedEvent.location}</div>
              </div>

              <div>
                <strong>Ngày:</strong>
                <div>{dayjs(selectedEvent.date).format("DD/MM/YYYY")}</div>
              </div>
            </div>

            {/* Dòng kẻ phân tách */}
            <div
              style={{ margin: "16px 0", borderTop: "1px solid #f0f0f0" }}
            ></div>

            {/* Mô tả & xử lý */}
            <div style={{ lineHeight: "1.8em" }}>
              <div>
                <strong>Mô tả:</strong>
                <div>{selectedEvent.description || "(Không có)"}</div>
              </div>
              <div style={{ marginTop: 12 }}>
                <strong>Xử lý:</strong>
                <div>{selectedEvent.actionsTaken || "(Không có)"}</div>
              </div>
            </div>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default MedicalEventList;