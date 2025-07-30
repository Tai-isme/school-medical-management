import React, { useRef, useState } from "react";
import { Button } from "antd";
import { PlusOutlined, FileExcelOutlined, UploadOutlined } from "@ant-design/icons";
import StudentList from "./StudentList";
import StudentProfileCard from "./StudentProfileCard";
import EmergencyContact from "./EmergencyContact";
import MedicalHistory from "./MedicalHistory";
import Swal from "sweetalert2";
export default function MedicalDashboard() {
  const [selectedStudent, setSelectedStudent] = useState(null);
  const fileInputRef = useRef(null);
  const [uploading, setUploading] = useState(false);

  // Handler mẫu cho các nút
  const handleAddStudent = () => {
    // Mở modal/thực hiện chức năng thêm học sinh
    // ...
  };

  const handleDownloadTemplate = () => {
    const link = document.createElement('a');
    link.href = '/student_template.xlsx';
    link.download = 'student_template.xlsx';
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
  };

  const handleImportExcel = () => {
    fileInputRef.current.click();
  };

  const handleFileChange = async (e) => {
    const file = e.target.files[0];
    if (!file) return;
    setUploading(true);
    const formData = new FormData();
    formData.append("file", file);

    try {
      const token = localStorage.getItem("token"); // Nếu cần token
      const response = await fetch("http://localhost:8080/api/admin/student/import-excel", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: formData,
      });

      if (!response.ok) {
        // Kiểm tra xem phản hồi có phải JSON hợp lệ không
        let errorMessage = "Import thất bại!";
        try {
          const errorData = await response.json(); // Lấy dữ liệu lỗi từ API nếu có
          errorMessage = errorData.message || errorMessage;
        } catch {
          // Nếu không phải JSON hợp lệ, sử dụng phản hồi dạng text
          const errorText = await response.text();
          errorMessage = errorText || errorMessage;
        }
        throw new Error(errorMessage);
      }

      // Hiển thị thông báo thành công
      Swal.fire({
        icon: "success",
        title: "Import thành công!",
        text: "Dữ liệu đã được cập nhật.",
        showConfirmButton: false,
        timer: 1500,
      });
    } catch (err) {
      // Hiển thị thông báo thất bại với lỗi chi tiết
      Swal.fire({
        icon: "error",
        title: "Import thất bại!",
        text: err.message, // Hiển thị lỗi chi tiết từ API hoặc phản hồi dạng text
        showConfirmButton: true,
      });
    } finally {
      setUploading(false);
      e.target.value = ""; // Reset input để chọn lại file nếu cần
    }
  };

  return (
    <div
      style={{
        minHeight: "100vh",
        width: "100%",
        marginLeft: 240, // đúng với width sidebar
        background: "#f4f8fb",
        fontFamily: "Segoe UI, Arial, sans-serif",
        overflowX: "hidden",
        padding: 0, // Xóa padding ngoài cùng
      }}
    >
      <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between", margin: 0, padding: 0 }}>
        <h2
          style={{
            textAlign: "left",
            fontWeight: "bold",
            fontSize: 24,
            margin: "0 0 0px 32px",
            letterSpacing: 1,
            color: "#1976d2",
          }}
        >
          Quản lý hồ sơ học sinh
        </h2>
        <div style={{ marginRight: 32, display: "flex", gap: 12, marginTop: 12, marginBottom: 10 }}>
          {/* <Button
            type="primary"
            icon={<PlusOutlined />}
            onClick={handleAddStudent}
          >
            Thêm học sinh
          </Button> */}
          <Button
            icon={<FileExcelOutlined />}
            onClick={handleDownloadTemplate}
          >
            Lấy biểu mẫu
          </Button>
          <Button
            icon={<UploadOutlined />}
            onClick={handleImportExcel}
            loading={uploading}
          >
            Import Excel
          </Button>
        </div>
      </div>
      <div
        style={{
          display: "flex",
          height: "calc(100vh - 64px)",
          gap: 14,
          padding: 0, // Xóa padding ngoài cùng
          margin: 0,  // Xóa margin ngoài cùng
          boxSizing: "border-box",
        }}
      >
        {/* Cột trái */}
        <div
          style={{
            flex: 2,
            borderRadius: 16,
            background: "#fff",
            boxShadow: "0 2px 16px #0001",
            minWidth: 340,
            maxWidth: 420,
            padding: 0,
            // height: "100%",
            // maxHeight: "calc(100vh - 120px)",
            // overflowY: "auto",
            margin: 0, // Xóa margin
          }}
        >
          <StudentList
            onSelect={student => setSelectedStudent(student)}
            selectedId={selectedStudent?.id}
            onFirstStudentLoaded={student => {
              if (!selectedStudent && student) setSelectedStudent(student);
            }}
          />
        </div>

        {/* Cột giữa */}
        
          <div >
            <StudentProfileCard
              studentId={selectedStudent?.studentId}
              studentInfo={selectedStudent}
            />
          </div>

        {/* Cột phải */}
        <div
          style={{
            flex: 2,
            display: "flex",
            flexDirection: "column",
            height: "910px",
            minWidth: 340,
            maxWidth: 420,
            gap: 10,
            overflowY: "auto",
            margin: 0, // Xóa margin
          }}
        >
          <div
            style={{
              border: "none",
              borderRadius: 16,
              background: "#fff",
              boxShadow: "0 2px 16px #0001",
              marginBottom: 0,
              overflow: "auto",
              padding: 0,
            }}
          >
            <EmergencyContact parentInfo={selectedStudent?.parentDTO} parentRole={selectedStudent} />
          </div>
          <div
            style={{
              border: "none",
              borderRadius: 16,
              background: "#fff",
              boxShadow: "0 2px 16px #0001",
              flex: 1,
              overflow: "auto",
              padding: 0,
            }}
          >
            <MedicalHistory studentId={selectedStudent?.studentId} />
          </div>
        </div>
      </div>
      <input
        type="file"
        accept=".xlsx,.xls"
        style={{ display: "none" }}
        ref={fileInputRef}
        onChange={handleFileChange}
      />
    </div>
  );
}