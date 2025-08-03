"use client"

import { useState } from "react" // Đã xóa useRef vì không còn dùng file input trực tiếp
import { Button } from "antd"
import { UploadOutlined } from "@ant-design/icons" // Đã xóa FileExcelOutlined vì nó được dùng trong modal

import StudentList from "./StudentList"
import StudentProfileCard from "./StudentProfileCard"
import EmergencyContact from "./EmergencyContact"
import MedicalHistory from "./MedicalHistory"
import GenericTemplateDownloadButton from "../VaccineProgramList/GenericTemplateDownloadButton" // Đảm bảo đường dẫn đúng
import StudentImportModal from "./StudentImportModal" // Đã cập nhật đường dẫn

export default function MedicalDashboard() {
  const [selectedStudent, setSelectedStudent] = useState(null)
  const [importStudentVisible, setImportStudentVisible] = useState(false) // State để điều khiển modal import học sinh
  const userRole = localStorage.getItem("role") // Lấy role từ localStorage

  // Hàm để refresh danh sách học sinh sau khi import thành công
  // Bạn cần triển khai logic này dựa trên cách StudentList của bạn tải dữ liệu
  const fetchStudentList = () => {
    console.log("Refreshing student list after successful import...")
    // Ví dụ: Nếu StudentList có một prop như `onRefresh` hoặc `refreshData`, bạn có thể gọi nó ở đây.
    // Hoặc nếu StudentList lấy dữ liệu từ một context/redux store, bạn có thể dispatch một action để tải lại dữ liệu.
    // Hiện tại, đây chỉ là một placeholder.
  }

  // Handler mẫu cho các nút
  const handleAddStudent = () => {
    // Mở modal/thực hiện chức năng thêm học sinh
    console.log("Thêm học sinh được click")
    // ...
  }

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
      <div
        style={{
          display: "flex",
          alignItems: "center",
          justifyContent: "space-between",
          margin: 0,
          padding: 0,
        }}
      >
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
        <div
          style={{
            marginRight: 32,
            display: "flex",
            gap: 12,
            marginTop: 12,
            marginBottom: 10,
          }}
        >
          {/* Nút Thêm học sinh (nếu bạn muốn sử dụng) */}
          {/* <Button
          type="primary"
          icon={<PlusOutlined />}
          onClick={handleAddStudent}
        >
          Thêm học sinh
        </Button> */}

          {/* Nút Lấy biểu mẫu Học sinh (sử dụng GenericTemplateDownloadButton) */}
          <GenericTemplateDownloadButton
            userRole={userRole}
            fileName="student_template.xlsx"
            filePath="/student_template.xlsx" // Đảm bảo file này tồn tại trong thư mục public/
            templateName="biểu mẫu học sinh"
            fileInfo={[
              "Định dạng: Excel (.xlsx)",
              "Kích thước: ~10KB",
              'Cột mẫu: "Mã học sinh", "Tên học sinh", "Lớp", "Ngày sinh"',
            ]}
            buttonText="Lấy biểu mẫu Học sinh"
          />

          {/* Nút Import Excel Học sinh (mở StudentImportModal) */}
          <Button
            icon={<UploadOutlined />}
            onClick={() => setImportStudentVisible(true)} // Mở modal import học sinh
            type="primary"
            style={{ backgroundColor: "#1890ff", borderColor: "#1890ff" }} // Màu xanh dương cho import
          >
            Import Excel Học sinh
          </Button>
        </div>
      </div>

      <div
        style={{
          display: "flex",
          height: "calc(100vh - 64px)", // Điều chỉnh chiều cao để phù hợp với header
          gap: 14,
          padding: 0, // Xóa padding ngoài cùng
          margin: 0, // Xóa margin ngoài cùng
          boxSizing: "border-box",
        }}
      >
        {/* Cột trái: Danh sách học sinh */}
        <div
          style={{
            flex: 2,
            borderRadius: 16,
            background: "#fff",
            boxShadow: "0 2px 16px #0001",
            minWidth: 340,
            maxWidth: 420,
            padding: 0,
            margin: 0, // Xóa margin
          }}
        >
          <StudentList
            onSelect={(student) => setSelectedStudent(student)}
            selectedId={selectedStudent?.id}
            onFirstStudentLoaded={(student) => {
              if (!selectedStudent && student) setSelectedStudent(student)
            }}
          />
        </div>

        {/* Cột giữa: Hồ sơ học sinh */}
        <div>
          <StudentProfileCard studentId={selectedStudent?.studentId} studentInfo={selectedStudent} />
        </div>

        {/* Cột phải: Thông tin khẩn cấp và Lịch sử y tế */}
        <div
          style={{
            flex: 2,
            display: "flex",
            flexDirection: "column",
            height: "910px", // Chiều cao cố định cho cột phải
            minWidth: 340,
            maxWidth: 420,
            gap: 10,
            overflowY: "auto", // Cho phép cuộn nếu nội dung dài
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

      {/* Modal Import Học sinh */}
      <StudentImportModal
        open={importStudentVisible}
        onClose={() => setImportStudentVisible(false)}
        onSuccess={fetchStudentList} // Gọi hàm này khi import thành công để refresh danh sách
      />
    </div>
  )
}
