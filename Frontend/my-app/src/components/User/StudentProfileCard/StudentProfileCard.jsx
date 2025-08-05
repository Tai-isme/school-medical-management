import React, { useState, useEffect } from "react";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import "./StudentProfileCard.css";
import axios from "axios";
import StudentInfoCard from "../../../common/StudentInfoCard";
import { Button, Modal, Input, Form, Table } from "antd"; // Import Button and Modal from antd
import MedicalRecordModal from "./MedicalRecordModal"; // Import MedicalRecordModal component
import { faHouse } from "@fortawesome/free-solid-svg-icons"; // Thêm dòng này vào đầu file
import dayjs from "dayjs"; // Import dayjs for date formatting
import { urlServer } from "../../../api/urlServer";

const StudentProfile = () => {
  const [activeTab, setActiveTab] = useState("general");
  const [isEditing, setIsEditing] = useState(false);
  const [students, setStudents] = useState([]);
  const [selectedStudentId, setSelectedStudentId] = useState();
  const [studentInfo, setStudentInfo] = useState({
    eyes: "",
    ears: "",
    weight: "",
    height: "",
    allergies: "",
    chronicDiseases: "",
    medicalHistory: "",
    lastUpdated: "",
    note: "",
  });
  const [vaccineHistory, setVaccineHistory] = useState([]);
  const [openMedicalForm, setOpenMedicalForm] = useState(false);
  const [editMode, setEditMode] = useState(false);
  const [form] = Form.useForm();
  const [loading, setLoading] = useState(false);
  const [hasMedicalRecord, setHasMedicalRecord] = useState(false);
  const [hasVaccineHistory, setHasVaccineHistory] = useState(false);

  // Lấy students từ localStorage
  useEffect(() => {
    const studentsData = JSON.parse(localStorage.getItem("students")) || [];
    setStudents(studentsData);

    const studentIdAlready = localStorage.getItem("studentIdAlready");
    if (studentIdAlready && studentIdAlready !== "null") {
      setSelectedStudentId(Number.studentIdAlready);
    }
  }, []);

  useEffect(() => {
    if (selectedStudentId) {
      fetchStudentInfo(selectedStudentId); // Gọi API lấy thông tin học sinh
    }
  }, [selectedStudentId]);

  const fetchStudentInfo = async (studentId) => {
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get(
        `${urlServer}/api/parent/medical-records/${studentId}`,
        { headers: { Authorization: `Bearer ${token}` } }
      );

      const medicalRecord = res.data.medicalRecord;
      const vaccineHistories = res.data.vaccineHistories;

      setHasMedicalRecord(!!medicalRecord);
      setHasVaccineHistory(
        Array.isArray(vaccineHistories) && vaccineHistories.length > 0
      );

      setStudentInfo({
        eyes: medicalRecord?.vision || "",
        ears: medicalRecord?.hearing || "",
        weight: medicalRecord?.weight || "",
        height: medicalRecord?.height || "",
        allergies: medicalRecord?.allergies || "",
        chronicDiseases: medicalRecord?.chronicDisease || "",
        medicalHistory: medicalRecord?.treatmentHistory || "",
        lastUpdated: medicalRecord?.lastUpdate || "",
        note: medicalRecord?.note || "",
        createBy: medicalRecord?.createBy || false,
      });

      setVaccineHistory(vaccineHistories || []);
    } catch (err) {
      setHasMedicalRecord(false);
      setHasVaccineHistory(false);
      setStudentInfo({
        eyes: "",
        ears: "",
        weight: "",
        height: "",
        allergies: "",
        chronicDiseases: "",
        medicalHistory: "",
        lastUpdated: "",
        note: "",
      });
      setVaccineHistory([]);
    }
  };

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setStudentInfo({
      ...studentInfo,
      [name]: value,
    });
  };

  const handleEditClick = () => {
    setEditMode(true);
    setOpenMedicalForm(true);
  };

  const handleCloseMedicalForm = () => {
    setOpenMedicalForm(false);
    setEditMode(false);
  };

  const handleSaveClick = async () => {
    const now = new Date().toISOString();

    setIsEditing(false);

    const body = {
      studentId: selectedStudentId,
      allergies: studentInfo.allergies,
      chronicDisease: studentInfo.chronicDiseases,
      treatmentHistory: studentInfo.medicalHistory,
      vision: studentInfo.eyes,
      hearing: studentInfo.ears,
      weight: Number(studentInfo.weight),
      height: Number(studentInfo.height),
      lastUpdate: now, // gửi đúng thời điểm hiện tại
      note: studentInfo.note,
    };

    try {
      const token = localStorage.getItem("token");
      await axios.put(
        `${urlServer}/api/parent/medical-records/${selectedStudentId}`,
        body,
        { headers: { Authorization: `Bearer ${token}` } }
      );
      // Sau khi lưu thành công, cập nhật lại state để hiển thị đúng thời điểm mới nhất
      setStudentInfo((prev) => ({
        ...prev,
        lastUpdated: now,
      }));
      alert("Cập nhật thành công!");
    } catch (err) {
      alert("Cập nhật thất bại!");
      console.error(err);
    }
  };

  const vaccineColumns = [
    {
      title: "Loại Vaccin đã tiêm",
      dataIndex: "vaccineName",
      key: "vaccineName",
      align: "center",
      width: 200,
      render: (text) => <span style={{ fontWeight: 500 }}>{text}</span>,
    },
    {
      title: "Mũi thứ",
      dataIndex: "unit",
      key: "unit",
      align: "center",
      width: 100,
      render: (unit) =>
        unit ? <span style={{ fontWeight: 500 }}>{`Mũi ${unit}`}</span> : "--",
    },
    {
      title: "Mô tả",
      dataIndex: "note",
      key: "note",
      align: "center",
      width: 250,
      render: (text) => (
        <span
          style={{
            whiteSpace: "pre-wrap",
            color: "#333",
            fontSize: 15,
            padding: "2px 0",
            display: "inline-block",
            minWidth: 80,
            maxWidth: 250,
            wordBreak: "break-word",
          }}
        >
          {text || "--"}
        </span>
      ),
    },
  ];

  const selectedStudent = students.find((s) => s.id === selectedStudentId);
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
        Hồ sơ học sinh
      </h1>

      <div className="tabs">
        <button
          className={`tab-button ${activeTab === "general" ? "active" : ""}`}
          onClick={() => setActiveTab("general")}
        >
          Thông tin chung
        </button>
        <button
          className={`tab-button ${activeTab === "vaccine" ? "active" : ""}`}
          onClick={() => setActiveTab("vaccine")}
        >
          Lịch sử tiêm vaccine
        </button>
      </div>

      <div className="profile-content">
        {/* Left Section: Student Overview */}
        <div className="left-panel">
          <StudentInfoCard onChange={setSelectedStudentId} />
        </div>

        {/* Right Section: Student Details / Vaccine History */}
        <div className="right-panel">
          {(!hasMedicalRecord && !hasVaccineHistory) ? (
            // Nếu cả hai đều rỗng thì mới cho khai báo
            <div
              style={{
                background: "#fffbe6",
                border: "1px solid #ffe58f",
                borderRadius: 8,
                padding: 32,
                textAlign: "center",
                marginTop: 24,
              }}
            >
              <div style={{ fontSize: 18, marginBottom: 16 }}>
                Hồ sơ sức khỏe của học sinh{" "}
                <b>{selectedStudent?.fullName || selectedStudent?.name}</b> chưa
                được khai báo.
                <br />
                Vui lòng nhấn vào nút bên dưới để khai báo hồ sơ học sinh.
              </div>
              <button
                style={{
                  background: "#1976d2",
                  color: "#fff",
                  border: "none",
                  borderRadius: 6,
                  padding: "10px 24px",
                  fontWeight: "bold",
                  fontSize: 16,
                  cursor: "pointer",
                }}
                onClick={() => setOpenMedicalForm(true)} // Viết hàm này để mở form khai báo
              >
                Tiến hành khai báo hồ sơ
              </button>
            </div>
          ) : (
            // Nếu 1 trong 2 có dữ liệu thì vẫn hiển thị thông tin, phần thiếu thì để rỗng
            <>
              {activeTab === "general" && (
                <>
                  <h2 style={{ margin: "0px 0px 20px 0px" }}>Hồ sơ sức khỏe</h2>
                  <div className="info-grid">
                    <div className="info-row">
                      <label>Thị giác</label>
                      <input
                        type="text"
                        name="eyes"
                        value={studentInfo.eyes}
                        onChange={handleInputChange}
                        readOnly={!isEditing}
                        className={isEditing ? "editable" : ""}
                      />
                    </div>
                    <div className="info-row">
                      <label>Thính giác</label>
                      <input
                        type="text"
                        name="ears"
                        value={studentInfo.ears}
                        onChange={handleInputChange}
                        readOnly={!isEditing}
                        className={isEditing ? "editable" : ""}
                      />
                    </div>
                    <div className="info-row">
                      <label>Cân nặng (kg)</label>
                      <input
                        type="text"
                        name="weight"
                        value={studentInfo.weight}
                        onChange={handleInputChange}
                        readOnly={!isEditing}
                        className={isEditing ? "editable" : ""}
                      />
                    </div>
                    <div className="info-row">
                      <label>Chiều cao (cm) </label>
                      <input
                        type="text"
                        name="height"
                        value={studentInfo.height}
                        onChange={handleInputChange}
                        readOnly={!isEditing}
                        className={isEditing ? "editable" : ""}
                      />
                    </div>
                    <div className="info-row">
                      <label>Dị ứng với</label>
                      <input
                        type="text"
                        name="allergies"
                        value={studentInfo.allergies}
                        onChange={handleInputChange}
                        readOnly={!isEditing}
                        className={isEditing ? "editable" : ""}
                      />
                    </div>
                    <div className="info-row">
                      <label>Bệnh mãn tính</label>
                      <input
                        type="text"
                        name="chronicDiseases"
                        value={studentInfo.chronicDiseases} // phải là chronicDiseases
                        onChange={handleInputChange}
                        readOnly={!isEditing}
                        className={isEditing ? "editable" : ""}
                      />
                    </div>

                    <div className="info-row">
                      <label>Lần cập nhật gần đây</label>
                      <input
                        type="text"
                        name="lastUpdated"
                        value={
                          studentInfo.lastUpdated
                            ? dayjs(studentInfo.lastUpdated).format(
                                "DD/MM/YYYY"
                              )
                            : ""
                        }
                        onChange={handleInputChange}
                        readOnly
                        className="read-only"
                      />
                    </div>
                    <div className="info-row full-width">
                      <label>Ghi chú</label>
                      <textarea
                        name="note"
                        value={studentInfo.note}
                        onChange={handleInputChange}
                        readOnly={!isEditing}
                        className={isEditing ? "editable" : ""}
                        rows="3"
                      ></textarea>
                    </div>
                    {/* Display the note if createBy is 1 */}
                    {studentInfo.createBy === true && (
                      <span
                        style={{
                          marginTop: "8px",
                          fontSize: "14px",
                          color: "#1976d2",
                        }}
                      >
                        Thông tin được tạo từ nhà trường.
                      </span>
                    )}
                  </div>
                </>
              )}

              {activeTab === "vaccine" && (
                <div className="vaccine-history-section">
                  <h2
                    style={{
                      textAlign: "center",
                      fontWeight: "bold",
                      marginBottom: 24,
                    }}
                  >
                    Các loại vaccin đã tiêm
                  </h2>
                  <Table
                    columns={vaccineColumns}
                    dataSource={vaccineHistory.map((item, idx) => ({
                      key: idx,
                      vaccineName: item.vaccineNameDTO?.vaccineName || "--",
                      unit: item.unit,
                      note: item.note,
                    }))}
                    pagination={{ pageSize: 5 }}
                    bordered
                    style={{
                      background: "#fff",
                      borderRadius: 8,
                      minWidth: 900,
                    }}
                  />
                </div>
              )}
            </>
          )}

          <div className="buttons-container" style={{ marginTop: "0px" }}>
            {activeTab === "general" && !( !hasMedicalRecord && !hasVaccineHistory ) && (
              <>
                {!isEditing ? (
                  <Button type="primary" onClick={handleEditClick}>
                    <FontAwesomeIcon icon="fa-solid fa-pen-to-square" /> Chỉnh sửa
                  </Button>
                ) : (
                  <Button type="primary" onClick={handleSaveClick}>
                    <FontAwesomeIcon icon="fa-solid fa-floppy-disk" /> Lưu
                  </Button>
                )}
              </>
            )}
          </div>
        </div>
      </div>

      <MedicalRecordModal
        open={openMedicalForm}
        onCancel={handleCloseMedicalForm}
        loading={loading}
        studentId={selectedStudentId}
        fetchStudentInfo={fetchStudentInfo}
        initialValues={{
          allergies: studentInfo.allergies,
          chronicDisease: studentInfo.chronicDiseases,
          vision: studentInfo.eyes,
          hearing: studentInfo.ears,
          weight: studentInfo.weight,
          height: studentInfo.height,
          note: studentInfo.note,
          vaccineHistories:
            vaccineHistory.length > 0
              ? vaccineHistory.map((v) => ({
                  vaccineName: v.vaccineNameDTO?.vaccineName || "",
                  vaccineNameId: v.vaccineNameDTO?.id || v.vaccineNameId || "",
                  doseNumber: v.unit || v.doseNumber || 1,
                  note: v.note || "",
                  createBy: v.createBy,
                }))
              : [], // <-- Không thêm dòng trống nữa!
        }}
        editMode={editMode}
      />
    </div>
  );
};

export default StudentProfile;
