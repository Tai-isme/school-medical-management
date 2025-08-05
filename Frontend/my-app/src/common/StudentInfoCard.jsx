import React, { useState, useEffect, useRef } from "react";
import { Select, Typography, Avatar, Button, message } from "antd";
import axios from "axios";
import dayjs from "dayjs";
const { Title, Text } = Typography;

export default function StudentInfoCard({ onChange }) {
  const [students, setStudents] = useState([]);
  const [selectedStudentId, setSelectedStudentId] = useState();
  const [uploading, setUploading] = useState(false);
  const [previewAvatar, setPreviewAvatar] = useState(null);
  const [selectedFile, setSelectedFile] = useState(null);
  const fileInputRef = useRef();

  useEffect(() => {
    const stored = JSON.parse(localStorage.getItem("students") || "[]");
    setStudents(stored);
    if (stored.length > 0) setSelectedStudentId(stored[0].studentId);
  }, []);

  useEffect(() => {
    if (onChange && selectedStudentId) onChange(selectedStudentId);
    setPreviewAvatar(null); // reset preview khi đổi học sinh
    setSelectedFile(null);
  }, [selectedStudentId, onChange]);

  const selectedStudent = students.find(
    (s) => s.studentId === selectedStudentId
  );

  // Khi chọn file, chỉ tạo preview
  const handleAvatarSelect = (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setSelectedFile(file);
    setPreviewAvatar(URL.createObjectURL(file));
  };

  // Khi bấm cập nhật, upload lên server
  const handleAvatarUpload = async () => {
    if (!selectedFile) return;
    setUploading(true);
    try {
      const formData = new FormData();
      formData.append("file", selectedFile);
      const token = localStorage.getItem("token");
      const res = await axios.post(
        "http://localhost:8080/api/upload/image",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${token}`,
          },
        }
      );
      const newStudents = students.map((s) =>
        s.studentId === selectedStudentId ? { ...s, avatar: res.data } : s
      );
      setStudents(newStudents);
      localStorage.setItem("students", JSON.stringify(newStudents));
      message.success("Cập nhật ảnh thành công!");
      setPreviewAvatar(null);
      setSelectedFile(null);
    } catch (err) {
      message.error("Upload ảnh thất bại!");
    } finally {
      setUploading(false);
    }
  };

  return (
    <div
      style={{
        background: "#f6fbff",
        borderRadius: 16,
        padding: 32,
        display: "flex",
        flexDirection: "column",
        alignItems: "center",
        minWidth: 452,
        minHeight: 520,
        height: "100%",
        boxSizing: "border-box",
        justifyContent: "center",
      }}
    >
      <Title level={3} style={{ color: "#1677ff", marginBottom: 24 }}>
        Học sinh
      </Title>

      <Select
        style={{ width: "100%", marginBottom: 32 }}
        value={selectedStudentId}
        onChange={setSelectedStudentId}
        options={students.map((s) => ({
          value: s.studentId,
          label: s.fullName || s.name || s.studentId,
        }))}
        placeholder="Chọn học sinh"
        size="large"
      />
      <div className="rgb-avatar-border" style={{ marginBottom: 16 }}>
        <Avatar
          size={200}
          src={
            previewAvatar ||
            selectedStudent?.avatarUrl ||
            "https://res.cloudinary.com/duzh5dnul/image/upload/v1750673843/6473ad42-3f20-4708-9bcf-76dff5d30ab2_avt2.jpg"
          }
        />
      </div>
      <input
        type="file"
        accept="image/*"
        ref={fileInputRef}
        style={{ display: "none" }}
        onChange={handleAvatarSelect}
      />

      <div style={{ display: "flex", flexDirection: "column", gap: 8 }}>
        <Text strong style={{ fontSize: 20, color: "#1C1C1E" }}>
          {selectedStudent?.fullName || selectedStudent?.name || "--"}
        </Text>

        <Text style={{ fontSize: 16 }}>
          🎓 <strong>Mã học sinh:</strong> {selectedStudent?.studentId || "--"}
        </Text>

        <Text style={{ fontSize: 16 }}>
          🗓️ <strong>Ngày sinh:</strong>{" "}
          {selectedStudent?.dob
            ? `${dayjs(selectedStudent.dob).format(
                "DD/MM/YYYY"
              )} (${dayjs().diff(dayjs(selectedStudent.dob), "year")} tuổi)`
            : "--"}
        </Text>

        <Text style={{ fontSize: 16 }}>
          🏫 <strong>Lớp:</strong>{" "}
          {selectedStudent?.classDTO?.className || "--"}
        </Text>

        <Text style={{ fontSize: 16 }}>
          🚻 <strong>Giới tính:</strong>{" "}
          {selectedStudent?.gender === "MALE"
            ? "Nam"
            : selectedStudent?.gender === "FEMALE"
            ? "Nữ"
            : "--"}
        </Text>
      </div>
    </div>
  );
}
