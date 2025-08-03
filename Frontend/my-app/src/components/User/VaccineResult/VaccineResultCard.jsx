import React, { useState, useEffect } from "react";
// import { Table } from 'antd';
import StudentInfoCard from "../../../common/StudentInfoCard";
import { Input, DatePicker, Row, Col, message } from "antd";
import dayjs from "dayjs";
import VaccineHistoryDetailModal from "./VaccineHistoryDetailModal";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faHouse } from "@fortawesome/free-solid-svg-icons";
// import './VaccineResult.css';

const VaccineResultCard = () => {
  const [students, setStudents] = useState([]);
  const [selectedStudentId, setSelectedStudentId] = useState();
  const [vaccineHistory, setVaccineHistory] = useState([]);
  const [modalOpen, setModalOpen] = useState(false);
  const [modalData, setModalData] = useState(null);
  const [modalLoading, setModalLoading] = useState(false); // Thêm state cho loading modal
  const [filterName, setFilterName] = useState("");
  const [filterDate, setFilterDate] = useState("");

  useEffect(() => {
    const studentsData = JSON.parse(localStorage.getItem("students")) || [];
    setStudents(studentsData);

    const studentIdAlready = localStorage.getItem("studentIdAlready");
    if (studentIdAlready && studentIdAlready !== "null") {
      setSelectedStudentId(Number(studentIdAlready));
    }
  }, []);

  useEffect(() => {
    if (selectedStudentId) {
      fetchVaccineHistory(selectedStudentId);
    }
  }, [selectedStudentId]);

  const fetchVaccineHistory = async (studentId) => {
    try {
      const token = localStorage.getItem("token");
      const res = await fetch(
        `http://localhost:8080/api/parent/vaccine-result/student/${studentId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      const data = await res.json();
      setVaccineHistory(Array.isArray(data) ? data : []);
    } catch (err) {
      setVaccineHistory([]);
    }
  };

  // Lọc dữ liệu nếu cần
  const filteredHistory = vaccineHistory.filter((item) => {
    const name =
      item.vaccineFormDTO?.vaccineNameDTO?.vaccineName?.toLowerCase() || "";
    const date = item.vaccineFormDTO?.vaccineProgramDTO?.startDate || "";
    const matchName = name.includes(filterName.toLowerCase());
    const matchDate = filterDate ? date === filterDate : true;
    return matchName && matchDate;
  });

  // Hàm lấy chi tiết vaccine result
  const handleShowDetail = (item) => {
    // item là một đối tượng hoàn chỉnh chứa tất cả thông tin
    // mà component VaccineHistoryDetailModal cần.
    setModalData([item]);
    setModalOpen(true);
  };

  return (
    <div className="student-profile-container" style={{ position: "relative" }}>
      {/* Nút Home ở góc trên trái */}
      <div
        style={{
          position: "absolute",
          top: 16,
          left: 32,
          display: "flex",
          alignItems: "center",
          zIndex: 10,
        }}
      >
        <button
          onClick={() => (window.location.href = "/")}
          style={{
            background: "#e3f2fd",
            border: "none",
            borderRadius: "50%",
            width: 40,
            height: 40,
            display: "flex",
            alignItems: "center",
            justifyContent: "center",
            boxShadow: "0 2px 8px #1976d220",
            cursor: "pointer",
            marginRight: 8,
          }}
          title="Về trang chủ"
        >
          <FontAwesomeIcon
            icon={faHouse}
            style={{ color: "#1976d2", fontSize: 22 }}
          />
        </button>
        <span
          style={{
            color: "#1976d2",
            fontWeight: 500,
            fontSize: 15,
            background: "#e3f2fd",
            borderRadius: 8,
            padding: "4px 14px",
            cursor: "pointer",
          }}
          onClick={() => (window.location.href = "/")}
          title="Về trang chủ"
        >
          Về trang chủ
        </span>
      </div>
      <h1
        className="main-title"
        style={{ margin: "0px 0px 20px 0px", fontSize: "24px" }}
      >
        Kết quả tiêm vaccine
      </h1>
      <div className="profile-content">
        {/* Left Section: Student Overview */}
        <div className="left-panel">
          <StudentInfoCard onChange={setSelectedStudentId} />
        </div>
        {/* Right Section: Vaccine History */}
        <div className="right-panel">
          <div className="vaccine-history-section">
            {/* Bộ lọc */}
            <div
              style={{
                display: "flex",
                justifyContent: "center",
                marginBottom: 24,
              }}
            >
              <div
                style={{
                  display: "flex",
                  gap: 12,
                  width: "100%",
                  maxWidth: 600,
                }}
              >
                <Input
                  placeholder="Lọc theo tên vaccine"
                  value={filterName}
                  onChange={(e) => setFilterName(e.target.value)}
                  allowClear
                />
                <DatePicker
                  placeholder="Lọc theo ngày"
                  value={filterDate ? dayjs(filterDate) : null}
                  onChange={(date) =>
                    setFilterDate(date ? date.format("YYYY-MM-DD") : "")
                  }
                  allowClear
                  style={{ width: 200 }}
                  format="YYYY-MM-DD"
                />
              </div>
            </div>

            {filteredHistory.length === 0 ? (
              <div
                style={{ textAlign: "center", color: "#888", marginTop: 32 }}
              >
                Không có dữ liệu phù hợp.
              </div>
            ) : (
              <>
                {/* Inline style for hover effect */}
                <style>{`
                  .vaccine-history-item {
                    background: #fff;
                    border-radius: 10px;
                    box-shadow: 0 2px 8px rgba(25,118,210,0.07);
                    padding: 20px;
                    margin-bottom: 8px;
                    cursor: pointer;
                    transition: border 0.2s, background 0.2s;
                    width: 100%;
                    min-width: 0;
                    align-self: stretch;
                    box-sizing: border-box;
                    display: block;
                    border: 1px solid #e3f2fd;
                  }
                  .vaccine-history-item.selected {
                    border: 2px solid #1976d2;
                  }
                  .vaccine-history-item:hover {
                    background: #e8f5e9;
                  }
                `}</style>
                <div
                  style={{
                    display: "flex",
                    flexDirection: "column",
                    gap: 16,
                    width: "100%",
                    alignItems: "stretch",
                    maxHeight: "62vh",
                    overflowY: "auto",
                    paddingRight: 8,
                  }}
                >
                  {filteredHistory.map((item, idx) => (
                    <div
                      key={idx}
                      className="vaccine-history-item"
                      onClick={() => handleShowDetail(item)}
                    >
                      <div
                        style={{
                          fontWeight: 600,
                          fontSize: 18,
                          color: "#1976d2",
                          marginBottom: 8,
                        }}
                      >
                        {item.vaccineFormDTO?.vaccineNameDTO?.vaccineName ||
                          "---"}
                      </div>
                      <div
                        style={{
                          display: "flex",
                          justifyContent: "space-between",
                          alignItems: "center",
                          width: "100%",
                          fontSize: 15,
                          color: "#888",
                        }}
                      >
                        <div>
                          Ngày:{" "}
                          <span style={{ color: "#1976d2", fontWeight: 500 }}>
                            {item.vaccineFormDTO?.vaccineProgramDTO?.startDate
                              ? item.vaccineFormDTO.vaccineProgramDTO.startDate
                                  .split("-")
                                  .reverse()
                                  .join("/")
                              : "---"}
                          </span>
                        </div>
                        <div>
                          Trạng thái:{" "}
                          <span style={{ color: "#43a047", fontWeight: 600 }}>
                            {item.vaccineFormDTO?.vaccineProgramDTO?.status ===
                            "COMPLETED"
                              ? "Đã hoàn thành"
                              : item.vaccineFormDTO?.vaccineProgramDTO
                                  ?.status || "---"}
                          </span>
                        </div>
                      </div>
                    </div>
                  ))}
                  <VaccineHistoryDetailModal
                    open={modalOpen}
                    onClose={() => setModalOpen(false)}
                    data={modalData}
                    loading={modalLoading}
                  />
                </div>
              </>
            )}
          </div>
        </div>
      </div>
    </div>
  );
};

export default VaccineResultCard;
