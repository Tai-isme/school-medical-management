import React from "react";
import { Modal, Button } from "antd";
import {
  ExclamationCircleOutlined,
  CalendarOutlined,
  FileTextOutlined,
  ToolOutlined,
  AlertOutlined,
  EnvironmentOutlined,
  UserOutlined,
  IdcardOutlined,
} from "@ant-design/icons";

const MedicalEventDetailModal = ({ visible, event, onClose }) => (
  <Modal
    open={visible}
    title={
      <span>
        <ExclamationCircleOutlined style={{ color: "#faad14", marginRight: 8, fontSize: 24 }} />
        Chi tiết sự kiện y tế
      </span>
    }
    footer={[
      <Button key="close" onClick={onClose}>
        Đóng
      </Button>
    ]}
    onCancel={onClose}
  >
    {event && (
      <div style={{ fontSize: 16, lineHeight: 2 }}>
        <p>
          <ExclamationCircleOutlined style={{ color: "#faad14", marginRight: 8 }} />
          <b>Loại sự kiện:</b> {event.typeEvent}
        </p>
        <p>
          <CalendarOutlined style={{ color: "#1890ff", marginRight: 8 }} />
          <b>Ngày diễn ra:</b> {new Date(event.date).toLocaleString("vi-VN")}
        </p>
        <p>
          <FileTextOutlined style={{ color: "#52c41a", marginRight: 8 }} />
          <b>Mô tả:</b> {event.description}
        </p>
        <p>
          <ToolOutlined style={{ color: "#722ed1", marginRight: 8 }} />
          <b>Hành động xử lý:</b> {event.actionsTaken}
        </p>
        <p>
          <AlertOutlined style={{ color: "#ff4d4f", marginRight: 8 }} />
          <b>Mức độ kiểm tra:</b> {event.levelCheck}
        </p>
        <p>
          <EnvironmentOutlined style={{ color: "#13c2c2", marginRight: 8 }} />
          <b>Địa điểm:</b> {event.location}
        </p>
        <p>
          <UserOutlined style={{ color: "#1890ff", marginRight: 8 }} />
          <b>Y tá phụ trách:</b> {event.nurseDTO?.fullName || "Không rõ"}
        </p>
        <p>
          <IdcardOutlined style={{ color: "#faad14", marginRight: 8 }} />
          <b>Học sinh:</b> {event.studentDTO?.fullName || ""}
        </p>
      </div>
    )}
  </Modal>
);

export default MedicalEventDetailModal;