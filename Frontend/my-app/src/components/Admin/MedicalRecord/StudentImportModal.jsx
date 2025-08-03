"use client"

import { useState, useRef } from "react"
import { Modal, Button, Progress, Alert, Space } from "antd"
import {
  UploadOutlined,
  FileExcelOutlined,
  CloseOutlined,
  CheckCircleOutlined,
  ExclamationCircleOutlined,
  LoadingOutlined,
  UsergroupAddOutlined, // Icon cho import học sinh
} from "@ant-design/icons"
import Swal from "sweetalert2"

const StudentImportModal = ({ open, onClose, onSuccess }) => {
  const [importFile, setImportFile] = useState(null)
  const [importLoading, setImportLoading] = useState(false)
  const [uploadProgress, setUploadProgress] = useState(0)
  const [dragActive, setDragActive] = useState(false)
  const [error, setError] = useState(null)
  const fileInputRef = useRef(null)

  const handleDrag = (e) => {
    e.preventDefault()
    e.stopPropagation()
    if (e.type === "dragenter" || e.type === "dragover") {
      setDragActive(true)
    } else if (e.type === "dragleave") {
      setDragActive(false)
    }
  }

  const handleDrop = (e) => {
    e.preventDefault()
    e.stopPropagation()
    setDragActive(false)
    setError(null)

    const files = e.dataTransfer.files
    if (files && files[0]) {
      validateAndSetFile(files[0])
    }
  }

  const handleFileChange = (e) => {
    setError(null)
    const files = e.target.files
    if (files && files[0]) {
      validateAndSetFile(files[0])
    }
  }

  const validateAndSetFile = (file) => {
    // Validate file type
    const allowedTypes = [
      "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet",
      "application/vnd.ms-excel",
    ]

    if (!allowedTypes.includes(file.type) && !file.name.match(/\.(xlsx|xls)$/i)) {
      setError("Chỉ chấp nhận file Excel (.xlsx, .xls)")
      return
    }

    // Validate file size (max 10MB)
    if (file.size > 10 * 1024 * 1024) {
      setError("File không được vượt quá 10MB")
      return
    }

    setImportFile(file)
  }

  const handleImport = async () => {
    if (!importFile) {
      setError("Vui lòng chọn file Excel!")
      return
    }

    const formData = new FormData()
    formData.append("file", importFile)
    setImportLoading(true)
    setUploadProgress(0)

    try {
      const token = localStorage.getItem("token")

      // Simulate progress for better UX
      const progressInterval = setInterval(() => {
        setUploadProgress((prev) => {
          if (prev >= 90) {
            clearInterval(progressInterval)
            return prev
          }
          return prev + 10
        })
      }, 200)

      const response = await fetch("http://localhost:8080/api/admin/student/import-excel", {
        method: "POST",
        headers: {
          Authorization: `Bearer ${token}`,
        },
        body: formData,
      })

      clearInterval(progressInterval)
      setUploadProgress(100)

      if (response.ok) {
        // Success notification
        Swal.fire({
          icon: "success",
          title: "Import thành công!",
          text: "Dữ liệu học sinh đã được cập nhật.",
          showConfirmButton: false,
          timer: 1500,
        })
        setTimeout(() => {
          onSuccess()
          handleClose()
        }, 500)
      } else {
        let errorMessage = "Import thất bại!"
        try {
          const errorData = await response.json()
          errorMessage = errorData.message || errorMessage
        } catch {
          const errorText = await response.text()
          errorMessage = errorText || errorMessage
        }
        throw new Error(errorMessage)
      }
    } catch (err) {
      setError(err.message || "Import thất bại! Vui lòng kiểm tra lại file.")
      Swal.fire({
        icon: "error",
        title: "Import thất bại!",
        text: err.message || "Có lỗi xảy ra khi import dữ liệu học sinh.",
        showConfirmButton: true,
      })
    } finally {
      setImportLoading(false)
      setTimeout(() => setUploadProgress(0), 1000)
    }
  }

  const handleClose = () => {
    setImportFile(null)
    setError(null)
    setUploadProgress(0)
    setImportLoading(false)
    if (fileInputRef.current) {
      fileInputRef.current.value = "" // Reset input file
    }
    onClose()
  }

  const removeFile = () => {
    setImportFile(null)
    setError(null)
    if (fileInputRef.current) {
      fileInputRef.current.value = ""
    }
  }

  const formatFileSize = (bytes) => {
    if (bytes === 0) return "0 Bytes"
    const k = 1024
    const sizes = ["Bytes", "KB", "MB", "GB"]
    const i = Math.floor(Math.log(bytes) / Math.log(k))
    return Number.parseFloat((bytes / Math.pow(k, i)).toFixed(2)) + " " + sizes[i]
  }

  return (
    <Modal
      title={
        <Space>
          <UsergroupAddOutlined style={{ color: "#1890ff", fontSize: 24 }} />
          <span style={{ color: "#1890ff", fontSize: 20, fontWeight: 600 }}>Import danh sách học sinh từ Excel</span>
        </Space>
      }
      open={open}
      onCancel={handleClose}
      footer={null}
      destroyOnClose
      width={520}
    >
      <div style={{ padding: "16px 0" }}>
        {/* File Upload Area */}
        <div
          style={{
            border: `2px dashed ${dragActive ? "#1890ff" : importFile ? "#52c41a" : "#d9d9d9"}`,
            borderRadius: 8,
            padding: 24,
            textAlign: "center",
            backgroundColor: dragActive ? "#f0f9ff" : importFile ? "#f6ffed" : "#fafafa",
            marginBottom: 16,
            position: "relative",
            transition: "all 0.3s ease",
          }}
          onDragEnter={handleDrag}
          onDragLeave={handleDrag}
          onDragOver={handleDrag}
          onDrop={handleDrop}
        >
          <input
            ref={fileInputRef}
            type="file"
            accept=".xlsx,.xls"
            onChange={handleFileChange}
            style={{
              position: "absolute",
              inset: 0,
              width: "100%",
              height: "100%",
              opacity: 0,
              cursor: "pointer",
            }}
            disabled={importLoading}
          />

          {!importFile ? (
            <div>
              <UploadOutlined style={{ fontSize: 48, color: "#bfbfbf", marginBottom: 16 }} />
              <div style={{ marginBottom: 8 }}>
                <span style={{ color: "#1890ff", fontWeight: 500 }}>Nhấp để chọn file</span>
                <span style={{ color: "#8c8c8c" }}> hoặc kéo thả file vào đây</span>
              </div>
              <div style={{ fontSize: 12, color: "#8c8c8c" }}>Chỉ chấp nhận file Excel (.xlsx, .xls) - Tối đa 10MB</div>
            </div>
          ) : (
            <div style={{ display: "flex", alignItems: "center", justifyContent: "space-between" }}>
              <div style={{ display: "flex", alignItems: "center", gap: 12 }}>
                <div
                  style={{
                    padding: 8,
                    backgroundColor: "#f6ffed",
                    borderRadius: 8,
                    border: "1px solid #b7eb8f",
                  }}
                >
                  <FileExcelOutlined style={{ fontSize: 24, color: "#52c41a" }} />
                </div>
                <div style={{ textAlign: "left" }}>
                  <div
                    style={{
                      fontWeight: 500,
                      fontSize: 14,
                      color: "#262626",
                      maxWidth: 200,
                      overflow: "hidden",
                      textOverflow: "ellipsis",
                      whiteSpace: "nowrap",
                    }}
                  >
                    {importFile.name}
                  </div>
                  <div style={{ fontSize: 12, color: "#8c8c8c" }}>{formatFileSize(importFile.size)}</div>
                </div>
              </div>
              {!importLoading && (
                <Button
                  type="text"
                  icon={<CloseOutlined />}
                  onClick={removeFile}
                  style={{ color: "#ff4d4f" }}
                  size="small"
                />
              )}
            </div>
          )}
        </div>

        {/* Progress Bar */}
        {importLoading && (
          <div style={{ marginBottom: 16 }}>
            <div
              style={{
                display: "flex",
                justifyContent: "space-between",
                marginBottom: 8,
                fontSize: 14,
              }}
            >
              <span>Đang import...</span>
              <span>{uploadProgress}%</span>
            </div>
            <Progress percent={uploadProgress} size="small" />
          </div>
        )}

        {/* Error Alert */}
        {error && (
          <Alert
            message={error}
            type="error"
            icon={<ExclamationCircleOutlined />}
            style={{ marginBottom: 16 }}
            showIcon
          />
        )}

        {/* Success Message */}
        {uploadProgress === 100 && !error && (
          <Alert
            message="Import thành công!"
            type="success"
            icon={<CheckCircleOutlined />}
            style={{ marginBottom: 16 }}
            showIcon
          />
        )}

        {/* Instructions */}
        <Alert
          message="Hướng dẫn:"
          description={
            <ul style={{ margin: 0, paddingLeft: 16 }}>
              <li>File Excel phải có các cột: "Mã học sinh", "Tên học sinh", "Lớp", "Ngày sinh"</li>
              <li>Dòng đầu tiên là tiêu đề cột</li>
              <li>Không được để trống các trường bắt buộc</li>
              <li>Tối đa 1000 dòng dữ liệu</li>
            </ul>
          }
          type="info"
          style={{ marginBottom: 24 }}
          showIcon
        />

        {/* Action Buttons */}
        <div style={{ display: "flex", gap: 12 }}>
          <Button onClick={handleClose} disabled={importLoading} style={{ flex: 1 }}>
            Hủy
          </Button>
          <Button
            type="primary"
            onClick={handleImport}
            disabled={!importFile || importLoading}
            style={{ flex: 1, backgroundColor: "#1890ff", borderColor: "#1890ff" }}
            icon={importLoading ? <LoadingOutlined /> : <UploadOutlined />}
          >
            {importLoading ? "Đang import..." : "Import"}
          </Button>
        </div>
      </div>
    </Modal>
  )
}

export default StudentImportModal
