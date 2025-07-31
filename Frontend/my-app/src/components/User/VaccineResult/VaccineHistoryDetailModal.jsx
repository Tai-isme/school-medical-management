import React from 'react';
import { Modal } from 'antd';

const VaccineHistoryDetailModal = ({ open, onClose, data, loading }) => {
  if (!data) return null;

  // Xử lý url vắc xin
  const vaccineUrl =
    data.vaccineName?.url
      ? (typeof data.vaccineName.url === "string" && data.vaccineName.url.startsWith("http")
          ? data.vaccineName.url
          : `https://${data.vaccineName.url}`)
      : null;

  // Lấy lịch nhắc lại từ vaccineUnitDTOs
  const schedule =
    Array.isArray(data.vaccineName?.vaccineUnitDTOs) && data.unit
      ? data.vaccineName.vaccineUnitDTOs.find(u => u.unit === data.unit)?.schedule
      : null;

  // Trạng thái tiêm
  const statusText = data.isRejected === true
    ? "Chưa tiêm"
    : "Đã tiêm";

  // Y tá phụ trách
  const nurseName =
    data.nurseName ||
    data.nurseDTO?.fullName ||
    data.vaccineName?.userDTO?.fullName ||
    data.vaccineName?.nurseDTO?.fullName ||
    "---";

  return (
    <Modal
      visible={open}
      onCancel={onClose}
      footer={null}
      title="Chi tiết kết quả tiêm vaccine"
      confirmLoading={loading}
    >
      <div>
        <p>
          <b>Tên chương trình:</b> {data.vaccineProgramName || data.vaccineName?.vaccineProgramName || "---"}
        </p>
        <p>
          <b>Tên vắc xin:</b> {data.vaccineName?.vaccineName || "---"}
        </p>
        <p>
          <b>Mũi tiêm:</b> {data.unit || "---"}
        </p>
        <p>
          <b>Lịch nhắc lại:</b> {schedule || "---"}
        </p>
        <p>
          <b>Ngày diễn ra:</b> {data.vaccineDate || "---"}
        </p>
        <p>
          <b>Y tá phụ trách:</b> {nurseName}
        </p>
        <p>
          <b>Trạng thái tiêm:</b> {statusText}
        </p>
        <p>
          <b>Phản ứng sau tiêm:</b> {data.reaction || "---"}
        </p>
        <p>
          <b>Hành động thực hiện:</b> {data.actionsTaken || "---"}
        </p>
        <p>
          <b>Kết quả ghi chú:</b> {data.resultNote || "---"}
        </p>
        {vaccineUrl && (
          <p>
            <b>Thông tin thêm:</b>{" "}
            <a
              href={vaccineUrl}
              target="_blank"
              rel="noopener noreferrer"
              style={{ color: '#1890ff', textDecoration: 'underline' }}
            >
              Xem thêm thông tin về vắc xin
            </a>
          </p>
        )}
      </div>
    </Modal>
  );
};

export default VaccineHistoryDetailModal;