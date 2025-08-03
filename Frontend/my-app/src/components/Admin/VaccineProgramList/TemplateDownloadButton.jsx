"use client"

import { useState } from "react"
import { Button, Modal } from "antd"
import { DownloadOutlined, CheckCircleOutlined, FileExcelOutlined } from "@ant-design/icons"

const TemplateDownloadButton = ({ userRole, className, style }) => {
  const [isDownloading, setIsDownloading] = useState(false)
  const [isDownloaded, setIsDownloaded] = useState(false)

  const [confirmVisible, setConfirmVisible] = useState(false);

  const handleDownload = async () => {
    console.log("handleDownload: Bắt đầu tải xuống...") // Debug: Bắt đầu tải
    setIsDownloading(true)

    try {
      // Simulate download delay for better UX
      await new Promise((resolve) => setTimeout(resolve, 800))

      const link = document.createElement("a")
      link.href = "/vaccine_name_import.xlsx"
      link.setAttribute("download", "vaccine_name_import.xlsx")
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)

      setIsDownloaded(true)
      console.log("handleDownload: Tải xuống thành công!") // Debug: Tải thành công

      // Show success notification
      Modal.success({
        title: "Tải xuống thành công!",
        content: "File mẫu đã được tải về máy của bạn.",
        okText: "Đóng",
        icon: <CheckCircleOutlined style={{ color: "#52c41a" }} />,
      })

      // Reset success state after 3 seconds
      setTimeout(() => setIsDownloaded(false), 3000)
    } catch (error) {
      console.error("handleDownload: Tải xuống thất bại!", error) // Debug: Tải thất bại
      // Show error notification
      Modal.error({
        title: "Tải xuống thất bại!",
        content: "Có lỗi xảy ra khi tải file. Vui lòng thử lại.",
        okText: "Đóng",
      })
    } finally {
      setIsDownloading(false)
      console.log("handleDownload: Kết thúc quá trình tải.") // Debug: Kết thúc
    }
  }

  const showConfirmModal = () => {
    setConfirmVisible(true);
  }

  console.log("TemplateDownloadButton: Component đang render. userRole:", userRole) // Debug: Component render

  if (userRole !== "ADMIN") {
    console.log("TemplateDownloadButton: userRole không phải ADMIN, không hiển thị nút.") // Debug: Không phải admin
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
          console.log("Button: onClick được kích hoạt.") // Debug: onClick kích hoạt
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
        {isDownloading ? "Đang tải..." : isDownloaded ? "Đã tải xong" : "Lấy biểu mẫu"}
      </Button>

      {/* Modal xác nhận tải xuống */}
      <Modal
        open={confirmVisible}
        onOk={async () => {
          setConfirmVisible(false);
          await handleDownload();
        }}
        onCancel={() => setConfirmVisible(false)}
        title="Xác nhận tải xuống"
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
        icon={<FileExcelOutlined style={{ color: "#52c41a" }} />}
      >
        <div style={{ marginTop: 16 }}>
          <p style={{ marginBottom: 12, fontSize: 14 }}>Bạn có muốn tải xuống file mẫu Excel không?</p>
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
              <li>Tên file: vaccine_name_import.xlsx</li>
              <li>Định dạng: Excel (.xlsx)</li>
              <li>Kích thước: ~15KB</li>
              <li>Cột mẫu: "Tên vaccine"</li>
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

export default TemplateDownloadButton
