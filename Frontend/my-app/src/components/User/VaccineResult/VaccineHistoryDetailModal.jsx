import React from 'react';
import { Modal } from 'antd';

const VaccineHistoryDetailModal = ({ open, onClose, data }) => (
  <Modal
    open={open}
    title={data?.vaccineName || 'Chi tiết tiêm vaccine'}
    onCancel={onClose}
    footer={null}
  >
    {data && (
      <div style={{ fontSize: 16, color: '#333' }}>
        <div><b>Ngày:</b> {data.date || '---'}</div>
        <div><b>Mô tả:</b> {data.note || 'Không có mô tả.'}</div>
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

export default VaccineHistoryDetailModal;