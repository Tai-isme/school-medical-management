import React from 'react';
import { Modal } from 'antd';

const VaccineHistoryDetailModal = ({ open, onClose, data, loading }) => {
  if (!data) return null;

  const vaccineUrl = data.vaccineName?.url.startsWith("http")
    ? data.vaccineName.url
    : `https://${data.vaccineName?.url}`;

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
          <b>Tên vắc xin:</b> {data.vaccineName?.vaccineName || "---"}
        </p>
        <p>
          <b>Nhà sản xuất:</b> {data.vaccineName?.manufacture || "---"}
        </p>
        <p>
          <b>Ngày tiêm:</b> {data.vaccineDate || "---"}
        </p>
        <p>
          <b>Trạng thái:</b> {data.status === "COMPLETED" ? "Đã hoàn thành" : data.status || "---"}
        </p>
       
        <p>
          <b>Tình trạng sức khỏe:</b> {data.statusHealth || "---"}
        </p>
        <p>
          <b>Kết quả ghi chú:</b> {data.resultNote || "---"}
        </p>
        <p>
          <b>Phản ứng:</b> {data.reaction || "---"}
        </p>
        {data.vaccineName?.url
          ? (
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
          )
          : null}
      </div>
    </Modal>
  );
};

export default VaccineHistoryDetailModal;