import React, { useState, useEffect, useRef } from "react";
import { Select, Typography, Avatar, Button, message } from "antd";
import axios from "axios";
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
    if (stored.length > 0) setSelectedStudentId(stored[0].id);
  }, []);

  useEffect(() => {
    if (onChange && selectedStudentId) onChange(selectedStudentId);
    setPreviewAvatar(null); // reset preview khi đổi học sinh
    setSelectedFile(null);
  }, [selectedStudentId, onChange]);

  const selectedStudent = students.find(s => s.id === selectedStudentId);

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
      const newStudents = students.map(s =>
        s.id === selectedStudentId ? { ...s, avatar: res.data } : s
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
        justifyContent: "center"
      }}
    >
      {/* <style>
        {`
        .rgb-avatar-border {
          padding: 6px;
          border-radius: 50%;
          background: conic-gradient(
            #ff0000, #ff9900, #33ff00, #00ffff, #0033ff, #9900ff, #ff0099, #ff0000
          );
          animation: rgb-spin 2s linear infinite;
          display: inline-block;
        }
        @keyframes rgb-spin {
          100% {
            transform: rotate(360deg);
          }
        }
        .rgb-avatar-border .ant-avatar {
          border: 4px solid #fff;
          background: #e3f2fd;
        }
        `}
      </style> */}
      <Title level={3} style={{ color: "#8ab4e6", marginBottom: 24 }}>Học sinh</Title>
      <Select
        style={{ width: "100%", marginBottom: 32 }}
        value={selectedStudentId}
        onChange={setSelectedStudentId}
        options={students.map(s => ({
          value: s.id,
          label: s.fullName || s.name || s.id
        }))}
        placeholder="Chọn học sinh"
        size="large"
      />
      <div className="rgb-avatar-border" style={{ marginBottom: 16 }}>
        <Avatar
          size={200}
          src={previewAvatar || selectedStudent?.avatar || "https://res.cloudinary.com/duzh5dnul/image/upload/v1750673843/6473ad42-3f20-4708-9bcf-76dff5d30ab2_avt2.jpg"}
        />
      </div>
      <input
        type="file"
        accept="image/*"
        ref={fileInputRef}
        style={{ display: "none" }}
        onChange={handleAvatarSelect}
      />
      {/* {previewAvatar ? (
        <Button
          onClick={handleAvatarUpload}
          loading={uploading}
          style={{ marginBottom: 16 }}
          type="primary"
        >
          Cập nhật
        </Button>
      ) : (
        <Button
          onClick={() => fileInputRef.current && fileInputRef.current.click()}
          loading={uploading}
          style={{ marginBottom: 16 }}
        >
          Chỉnh sửa ảnh
        </Button>
      )} */}
      <Text strong style={{ fontSize: 20, marginBottom: 8 }}>
        {selectedStudent?.fullName || selectedStudent?.name || "--"}
      </Text>
      <Text style={{ fontSize: 16, marginBottom: 8 }}>
        Mã số: {selectedStudent?.id || "--"}
      </Text>
      <Text type="secondary" style={{ fontSize: 16, marginBottom: 8 }}>
        Lớp: {selectedStudent?.className || selectedStudent?.classID || "--"}
      </Text>
      <Text type="secondary" style={{ fontSize: 16 }}>
        Giới tính: {selectedStudent?.gender || "--"}
      </Text>
    </div>
  );
}