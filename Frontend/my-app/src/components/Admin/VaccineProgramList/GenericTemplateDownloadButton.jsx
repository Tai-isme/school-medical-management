"use client"

import { useState } from "react"
import { Button, Modal, Space } from "antd" // Import Space
import { DownloadOutlined, CheckCircleOutlined, FileExcelOutlined } from "@ant-design/icons"

const GenericTemplateDownloadButton = ({
  fileName, // Tên file sẽ tải xuống (ví dụ: "medical_event_template.xlsx")
  filePath, // Đường dẫn tới file trong thư mục public (ví dụ: "/medical_event_template.xlsx")
  templateName, // Tên thân thiện của biểu mẫu (ví dụ: "biểu mẫu sự kiện y tế")
  fileInfo = [], // Mảng các chuỗi thông tin thêm về file (ví dụ: ["Cột: Tên sự kiện, Loại sự kiện", "Kích thước: ~20KB"])
  buttonText = "Lấy biểu mẫu", // Text hiển thị trên nút
  userRole, // Optional: Nếu cần kiểm tra quyền ADMIN
  className,
  style,
}) => {
  const [isDownloading, setIsDownloading] = useState(false)
  const [isDownloaded, setIsDownloaded] = useState(false)
  const [confirmVisible, setConfirmVisible] = useState(false) // State để điều khiển Modal

  const handleDownload = async () => {
    console.log("handleDownload: Bắt đầu tải xuống...")
    setIsDownloading(true)
    setConfirmVisible(false) // Đóng modal xác nhận ngay khi bắt đầu tải

    try {
      // Simulate download delay for better UX
      await new Promise((resolve) => setTimeout(resolve, 800))

      const link = document.createElement("a")
      link.href = filePath // Sử dụng filePath từ props
      link.setAttribute("download", fileName) // Sử dụng fileName từ props
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)

      setIsDownloaded(true)
      console.log("handleDownload: Tải xuống thành công!")

      Modal.success({
        title: "Tải xuống thành công!",
        content: `File mẫu "${templateName}" đã được tải về máy của bạn.`, // Sử dụng templateName
        okText: "Đóng",
        icon: <CheckCircleOutlined style={{ color: "#52c41a" }} />,
      })

      setTimeout(() => setIsDownloaded(false), 3000)
    } catch (error) {
      console.error("Download failed:", error)
      Modal.error({
        title: "Tải xuống thất bại!",
        content: "Có lỗi xảy ra khi tải file. Vui lòng thử lại.",
        okText: "Đóng",
      })
    } finally {
      setIsDownloading(false)
      console.log("handleDownload: Kết thúc quá trình tải.")
    }
  }

  const showConfirmModal = () => {
    console.log("showConfirmModal: Đang mở modal xác nhận...")
    setConfirmVisible(true)
  }

  console.log("GenericTemplateDownloadButton: Component đang render. userRole:", userRole)

  // Nếu có userRole và không phải ADMIN thì không hiển thị nút
  if (userRole && userRole !== "ADMIN") {
    console.log("GenericTemplateDownloadButton: userRole không phải ADMIN, không hiển thị nút.")
    return null
  }

  return (
    <>
      <Button
        type="default"
        icon={
          isDownloading ? (
            <div
              style={{
                width: 14,
                height: 14,
                border: "2px solid #21ba45",
                borderTop: "2px solid transparent",
                borderRadius: "50%",
                animation: "spin 1s linear infinite",
              }}
            />
          ) : isDownloaded ? (
            <CheckCircleOutlined />
          ) : (
            <DownloadOutlined />
          )
        }
        onClick={() => {
          console.log("Button: onClick được kích hoạt.")
          showConfirmModal()
        }}
        disabled={isDownloading}
        className={className}
        style={{
          border: "1.5px solid #21ba45",
          color: isDownloaded ? "#389e0d" : "#21ba45",
          background: isDownloaded ? "#f6ffed" : "#fff",
          fontWeight: 600,
          marginRight: 12,
          transition: "all 0.3s ease",
          boxShadow: "0 2px 4px rgba(33, 186, 69, 0.1)",
          ...style,
          ...(isDownloading && {
            borderColor: "#73d13d",
            color: "#73d13d",
          }),
        }}
        onMouseEnter={(e) => {
          if (!isDownloading) {
            e.target.style.backgroundColor = "#f6ffed"
            e.target.style.borderColor = "#389e0d"
            e.target.style.color = "#389e0d"
            e.target.style.transform = "translateY(-1px)"
            e.target.style.boxShadow = "0 4px 8px rgba(33, 186, 69, 0.15)"
          }
        }}
        onMouseLeave={(e) => {
          if (!isDownloading && !isDownloaded) {
            e.target.style.backgroundColor = "#fff"
            e.target.style.borderColor = "#21ba45"
            e.target.style.color = "#21ba45"
            e.target.style.transform = "translateY(0)"
            e.target.style.boxShadow = "0 2px 4px rgba(33, 186, 69, 0.1)"
          }
        }}
      >
        {isDownloading ? "Đang tải..." : isDownloaded ? "Đã tải xong" : buttonText}
      </Button>

      {/* Modal xác nhận tải xuống */}
      <Modal
        open={confirmVisible}
        onOk={handleDownload} // Gọi handleDownload trực tiếp
        onCancel={() => setConfirmVisible(false)}
        title={
          <Space>
            <FileExcelOutlined style={{ color: "#52c41a", fontSize: 20 }} />
            Xác nhận tải xuống
          </Space>
        }
        okText="Tải xuống"
        cancelText="Hủy"
        okButtonProps={{
          icon: <DownloadOutlined />,
          style: {
            backgroundColor: "#52c41a",
            borderColor: "#52c41a",
          },
        }}
        cancelButtonProps={{
          style: {
            borderColor: "#d9d9d9",
          },
        }}
        width={480}
        centered
      >
        <div style={{ marginTop: 16 }}>
          <p style={{ marginBottom: 12, fontSize: 14 }}>Bạn có muốn tải xuống {templateName} không?</p>
          <div
            style={{
              backgroundColor: "#f6ffed",
              border: "1px solid #b7eb8f",
              borderRadius: 6,
              padding: 12,
              fontSize: 13,
            }}
          >
            <div style={{ fontWeight: 500, marginBottom: 8, color: "#389e0d" }}>📋 Thông tin file:</div>
            <ul style={{ margin: 0, paddingLeft: 16, color: "#52c41a" }}>
              <li>Tên file: {fileName}</li>
              {fileInfo.map((info, index) => (
                <li key={index}>{info}</li>
              ))}
            </ul>
          </div>
        </div>
      </Modal>

      {/* CSS Animation for spinner */}
      <style jsx>{`
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
      `}</style>
    </>
  )
}

export default GenericTemplateDownloadButton

