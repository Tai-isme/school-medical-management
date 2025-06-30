import React from 'react';
import { Modal } from 'antd';

const HealthCheckResultDetailModal = ({ open, onClose, data }) => (
  <Modal
    open={open}
    title={data?.checkName || 'Chi tiết khám sức khỏe'}
    onCancel={onClose}
    footer={null}
  >
    {data && (
      <div style={{ fontSize: 16, color: '#333' }}>
        <div><b>Ngày:</b> {data.date || '---'}</div>
        <div><b>Kết quả:</b> {data.result || 'Không có kết quả.'}</div>
        <div><b>Ghi chú:</b> {data.note || 'Không có ghi chú.'}</div>
        {data.place && <div><b>Địa điểm:</b> {data.place}</div>}
        {data.doctor && <div><b>Bác sĩ:</b> {data.doctor}</div>}
        <div>
          <b>Trạng thái:</b>{' '}
          <span style={{ color: '#43a047', fontWeight: 600 }}>Hoàn thành</span>
        </div>
      </div>
    )}
  </Modal>
);

export default HealthCheckResultDetailModal;