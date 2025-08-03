"use client"

import { useState } from "react"
import { Button, Modal, Space } from "antd"
import { DownloadOutlined, CheckCircleOutlined, FileExcelOutlined } from "@ant-design/icons"
import axios from "axios" // Đảm bảo bạn đã cài đặt axios

const ExportResultButton = ({
  vaccineProgramId,
  userRole,
  className,
  style,
  buttonText = "Xuất kết quả ra Excel",
  confirmTitle = "Xác nhận xuất kết quả",
  confirmContent = "Bạn có muốn xuất kết quả tiêm chủng ra file Excel không?",
  fileInfo = [], // Thông tin thêm về file xuất (ví dụ: cột, kích thước ước tính)
}) => {
  const [isDownloading, setIsDownloading] = useState(false)
  const [isDownloaded, setIsDownloaded] = useState(false)
  const [confirmVisible, setConfirmVisible] = useState(false)

  const handleExport = async () => {
    setIsDownloading(true)
    setConfirmVisible(false) // Đóng modal xác nhận ngay khi bắt đầu xuất

    const token = localStorage.getItem("token")
    try {
      const response = await axios.post(
        `http://localhost:8080/api/admin/export-vaccine-result-excel-by-vaccine-program/${vaccineProgramId}`,
        {}, // Body rỗng nếu API không yêu cầu
        {
          responseType: "blob", // Quan trọng để nhận dữ liệu nhị phân
          headers: { Authorization: `Bearer ${token}` },
        },
      )

      // Tạo link tải file
      const url = window.URL.createObjectURL(new Blob([response.data]))
      const link = document.createElement("a")
      link.href = url
      link.setAttribute("download", `ket-qua-tiem-chung-${vaccineProgramId}.xlsx`) // Tên file động
      document.body.appendChild(link)
      link.click()
      document.body.removeChild(link)
      window.URL.revokeObjectURL(url) // Giải phóng URL object

      setIsDownloaded(true)
      Modal.success({
        title: "Xuất file thành công!",
        content: "Kết quả tiêm chủng đã được xuất ra file Excel.",
        okText: "Đóng",
        icon: <CheckCircleOutlined style={{ color: "#52c41a" }} />,
      })
      setTimeout(() => setIsDownloaded(false), 3000)
    } catch (error) {
      console.error("Export failed:", error)
      Modal.error({
        title: "Xuất file thất bại!",
        content: error.response?.data?.message || "Có lỗi xảy ra khi xuất file. Vui lòng thử lại.",
        okText: "Đóng",
      })
    } finally {
      setIsDownloading(false)
    }
  }

  const showConfirmModal = () => {
    setConfirmVisible(true)
  }

  if (userRole !== "ADMIN") {
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
        onClick={showConfirmModal}
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
        {isDownloading ? "Đang xuất..." : isDownloaded ? "Đã xuất xong" : buttonText}
      </Button>

      <Modal
        open={confirmVisible}
        onOk={handleExport}
        onCancel={() => setConfirmVisible(false)}
        title={
          <Space>
            <FileExcelOutlined style={{ color: "#52c41a", fontSize: 20 }} />
            {confirmTitle}
          </Space>
        }
        okText="Xuất file"
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
          <p style={{ marginBottom: 12, fontSize: 14 }}>{confirmContent}</p>
          {fileInfo.length > 0 && (
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
                {fileInfo.map((info, index) => (
                  <li key={index}>{info}</li>
                ))}
              </ul>
            </div>
          )}
        </div>
      </Modal>

      <style jsx>{`
        @keyframes spin {
          0% { transform: rotate(0deg); }
          100% { transform: rotate(360deg); }
        }
      `}</style>
    </>
  )
}

export default ExportResultButton
