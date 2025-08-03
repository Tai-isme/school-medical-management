"use client"


import { Modal, Button, Descriptions, Tag, Typography, Space, Image } from "antd"
import {
  InfoCircleOutlined,
  CalendarOutlined,
  EnvironmentOutlined,
  UserOutlined,
  SolutionOutlined,
  TagOutlined,
  FileTextOutlined,
  PictureOutlined,
  EditOutlined,
  CloseOutlined,
  MedicineBoxOutlined, // Icon cho sự kiện y tế
} from "@ant-design/icons"
import dayjs from "dayjs"
import "dayjs/locale/vi" // Import locale tiếng Việt cho dayjs


dayjs.locale("vi") // Đặt locale mặc định là tiếng Việt


const { Paragraph } = Typography


const MedicalEventDetailModal = ({
  detailModalVisible,
  setDetailModalVisible,
  selectedEvent,
  handleEditClick,
  setPreviewImage, // Prop để set ảnh preview
  setPreviewVisible, // Prop để mở modal preview ảnh
}) => {
  // Hàm để lấy style cho levelCheck
  const getLevelTagColor = (level) => {
    switch (level) {
      case "Khẩn cấp":
        return "red"
      case "Nghiêm trọng":
        return "orange"
      case "Bình thường":
        return "green"
      default:
        return "blue"
    }
  }


  return (
    <Modal
      title={
        <Space>
          <MedicineBoxOutlined style={{ color: "#1476d1", fontSize: 24 }} />
          <span style={{ color: "#1476d1", fontSize: 20, fontWeight: 600 }}>Chi tiết sự kiện y tế</span>
        </Space>
      }
      open={detailModalVisible}
      onCancel={() => setDetailModalVisible(false)}
      footer={[
        <Button key="close" onClick={() => setDetailModalVisible(false)} icon={<CloseOutlined />}>
          Đóng
        </Button>,
        <Button
          key="edit"
          type="primary"
          onClick={() => handleEditClick(selectedEvent)}
          icon={<EditOutlined />}
          style={{ backgroundColor: "#1890ff", borderColor: "#1890ff" }}
        >
          Sửa
        </Button>,
      ]}
      width={720} // Tăng chiều rộng modal để hiển thị đẹp hơn
      bodyStyle={{
        padding: 24,
        backgroundColor: "#fefefe",
        borderRadius: 8,
      }}
    >
      {selectedEvent && (
        <div style={{ position: "relative" }}>
          {/* Level Tag */}
          <div style={{ position: "absolute", top: -10, right: 0, zIndex: 1 }}>
            <Tag color={getLevelTagColor(selectedEvent.levelCheck)} style={{ fontSize: 14, padding: "4px 10px" }}>
              {selectedEvent.levelCheck}
            </Tag>
          </div>


          {/* Basic Information */}
          <Descriptions
            bordered
            column={{ xxl: 2, xl: 2, lg: 2, md: 2, sm: 1, xs: 1 }}
            size="middle"
            style={{ marginBottom: 24 }}
          >
            <Descriptions.Item
              label={
                <Space>
                  <InfoCircleOutlined /> Tên sự kiện
                </Space>
              }
            >
              {selectedEvent.eventName}
            </Descriptions.Item>
            <Descriptions.Item
              label={
                <Space>
                  <TagOutlined /> Loại sự kiện
                </Space>
              }
            >
              {selectedEvent.typeEvent}
            </Descriptions.Item>
            <Descriptions.Item
              label={
                <Space>
                  <UserOutlined /> Học sinh
                </Space>
              }
            >
              {selectedEvent.studentName}
            </Descriptions.Item>
            <Descriptions.Item
              label={
                <Space>
                  <SolutionOutlined /> Lớp
                </Space>
              }
            >
              {selectedEvent.className}
            </Descriptions.Item>
            <Descriptions.Item
              label={
                <Space>
                  <UserOutlined /> Y tá phụ trách
                </Space>
              }
            >
              {selectedEvent.nurseName}
            </Descriptions.Item>
            <Descriptions.Item
              label={
                <Space>
                  <EnvironmentOutlined /> Địa điểm
                </Space>
              }
            >
              {selectedEvent.location}
            </Descriptions.Item>
            <Descriptions.Item
              label={
                <Space>
                  <CalendarOutlined /> Ngày
                </Space>
              }
            >
              {dayjs(selectedEvent.date).format("DD/MM/YYYY HH:mm")}
            </Descriptions.Item>
          </Descriptions>


          {/* Description and Actions Taken */}
          <div style={{ marginBottom: 24 }}>
            <h4 style={{ marginBottom: 8, fontSize: 16, fontWeight: 500, color: "#333" }}>
              <FileTextOutlined style={{ marginRight: 8, color: "#1890ff" }} /> Mô tả:
            </h4>
            <Paragraph style={{ whiteSpace: "pre-wrap", color: "#555" }}>
              {selectedEvent.description || "(Không có mô tả)"}
            </Paragraph>


            <h4 style={{ marginTop: 16, marginBottom: 8, fontSize: 16, fontWeight: 500, color: "#333" }}>
              <SolutionOutlined style={{ marginRight: 8, color: "#52c41a" }} /> Xử lý:
            </h4>
            <Paragraph style={{ whiteSpace: "pre-wrap", color: "#555" }}>
              {selectedEvent.actionsTaken || "(Chưa có xử lý)"}
            </Paragraph>
          </div>


          {/* Image Display */}
          {selectedEvent.image && (
            <div style={{ marginTop: 16 }}>
              <h4 style={{ marginBottom: 8, fontSize: 16, fontWeight: 500, color: "#333" }}>
                <PictureOutlined style={{ marginRight: 8, color: "#eb2f96" }} /> Hình ảnh sự cố:
              </h4>
              <div
                style={{
                  border: "1px solid #f0f0f0",
                  borderRadius: 8,
                  padding: 12,
                  backgroundColor: "#fff",
                  display: "inline-block", // Để container ôm sát ảnh
                }}
              >
                <Image
                  src={selectedEvent.image || "/placeholder.svg"}
                  alt="Ảnh sự kiện"
                  style={{
                    maxWidth: "100%", // Đảm bảo ảnh không tràn ra ngoài
                    maxHeight: 300, // Giới hạn chiều cao ảnh
                    borderRadius: 4,
                    cursor: "pointer",
                    objectFit: "contain", // Giữ tỷ lệ ảnh
                  }}
                  preview={{
                    visible: false, // Tắt preview mặc định của Ant Design Image
                    onVisibleChange: (visible) => setPreviewVisible(visible),
                  }}
                  onClick={() => {
                    setPreviewImage(selectedEvent.image)
                    setPreviewVisible(true)
                  }}
                />
              </div>
            </div>
          )}
        </div>
      )}
    </Modal>
  )
}


export default MedicalEventDetailModal



