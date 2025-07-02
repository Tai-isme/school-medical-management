import React from 'react';
import { Modal, Spin } from 'antd';

const labelStyle = {
  fontWeight: 600,
  color: '#1976d2',
  minWidth: 120,
  display: 'inline-block',
  marginBottom: 4,
};

const valueStyle = {
  color: '#222',
  fontWeight: 500,
  marginLeft: 8,
  wordBreak: 'break-word',
};

const sectionStyle = {
  marginBottom: 12,
  display: 'flex',
  alignItems: 'flex-start',
};

const VaccineHistoryDetailModal = ({ open, onClose, data, loading }) => (
  <Modal
    open={open}
    title={
      <div style={{ textAlign: 'center', fontWeight: 700, fontSize: 22, color: '#1976d2', letterSpacing: 1 }}>
        {data?.vaccineName || 'Chi tiết tiêm vaccine'}
      </div>
    }
    onCancel={onClose}
    footer={null}
    centered
    bodyStyle={{
      padding: 32,
      background: '#f8fbff',
      borderRadius: 16,
      minHeight: 320,
    }}
    width={480}
  >
    {loading ? (
      <div style={{ textAlign: "center", padding: 48 }}><Spin size="large" /></div>
    ) : data ? (
      <div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Tên vaccine:</span>
          <span style={valueStyle}>{data.vaccineName || "---"}</span>
        </div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Ngày tiêm:</span>
          <span style={valueStyle}>{data.vaccineDate || "---"}</span>
        </div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Trạng thái:</span>
          <span style={{
            ...valueStyle,
            color: data.status === "COMPLETED" ? "#43a047" : "#fbc02d"
          }}>
            {data.status || "---"}
          </span>
        </div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Mô tả:</span>
          <span style={valueStyle}>{data.description || "Không có mô tả."}</span>
        </div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Ghi chú:</span>
          <span style={valueStyle}>{data.note || "---"}</span>
        </div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Kết quả sức khỏe:</span>
          <span style={valueStyle}>{data.statusHealth || "---"}</span>
        </div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Phản ứng:</span>
          <span style={valueStyle}>{data.reaction || "---"}</span>
        </div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Ghi chú kết quả:</span>
          <span style={valueStyle}>{data.resultNote || "---"}</span>
        </div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Ngày ghi nhận:</span>
          <span style={valueStyle}>{data.createdAt ? new Date(data.createdAt).toLocaleString() : "---"}</span>
        </div>
      </div>
    ) : null}
  </Modal>
);

export default VaccineHistoryDetailModal;