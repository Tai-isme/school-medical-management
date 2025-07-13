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

const HealthCheckResultDetailModal = ({ open, onClose, data, loading }) => (
  <Modal
    open={open}
    title={
      <div style={{ textAlign: 'center', fontWeight: 700, fontSize: 22, color: '#1976d2', letterSpacing: 1 }}>
        {data?.checkName || 'Chi tiết khám sức khỏe'}
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
          <span style={labelStyle}>Chương trình:</span>
          <span style={valueStyle}>{data.checkName || "---"}</span>
        </div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Ngày:</span>
          <span style={valueStyle}>{data.date || "---"}</span>
        </div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Trạng thái:</span>
          <span style={{
            ...valueStyle,
            color: data.status === "DONE" ? "#43a047" : "#fbc02d"
          }}>
            {data.healthCheckProgram.status || "---"}
          </span>
        </div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Mô tả:</span>
          <span style={valueStyle}>{data.description || "Không có mô tả."}</span>
        </div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Chẩn đoán:</span>
          <span style={valueStyle}>{data.diagnosis || "---"}</span>
        </div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Mức độ:</span>
          <span style={valueStyle}>{data.level || "---"}</span>
        </div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Ghi chú:</span>
          <span style={valueStyle}>{data.note || "---"}</span>
        </div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Thị lực:</span>
          <span style={valueStyle}>{data.vision || "---"}</span>
        </div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Thính lực:</span>
          <span style={valueStyle}>{data.hearing || "---"}</span>
        </div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Cân nặng:</span>
          <span style={valueStyle}>{data.weight != null ? `${data.weight} kg` : "---"}</span>
        </div>
        <div style={sectionStyle}>
          <span style={labelStyle}>Chiều cao:</span>
          <span style={valueStyle}>{data.height != null ? `${data.height} cm` : "---"}</span>
        </div>
      </div>
    ) : null}
  </Modal>
);

export default HealthCheckResultDetailModal;