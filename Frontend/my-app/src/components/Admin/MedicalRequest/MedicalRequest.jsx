import React, { useState, useEffect } from 'react';
import { Button, Modal, Select, message } from 'antd';
import axios from 'axios';
import './MedicalRequest.css';

const { Option } = Select;
const statusOptions = ["Chấp nhận", "Hoàn thành", "Đang xử lý", "Từ chối"];

const MedicalRequest = () => {
  const [data, setData] = useState([]);
  const [selected, setSelected] = useState(null);
  const [isModalVisible, setIsModalVisible] = useState(false);

  const token = localStorage.getItem('token');

  useEffect(() => {
    axios.get('http://localhost:8080/api/nurse/medical-request', {
      headers: {
        Authorization: `Bearer ${token}`,
      },
    })
      .then(res => {
        setData(res.data);
      })
      .catch(err => {
        message.error('Lỗi khi tải danh sách yêu cầu thuốc');
        console.error(err);
      });
  }, [token]);

  const openDetail = (record) => {
    setSelected(record);
    setIsModalVisible(true);
  };

  const handleStatusChange = (value) => {
    const updated = data.map(item =>
      item.requestId === selected.requestId ? { ...item, status: value } : item
    );
    setData(updated);
    setSelected(prev => ({ ...prev, status: value }));
    message.success(`Trạng thái đơn #${selected.requestId} đã đổi thành "${value}"`);
  };

  const handleAccept = (id) => {
    const updated = data.map(item =>
      item.requestId === id ? { ...item, status: "Đang xử lý" } : item
    );
    setData(updated);
    message.success(`Đơn #${id} đã được chấp nhận.`);
  };

  const handleReject = (id) => {
    const updated = data.map(item =>
      item.requestId === id ? { ...item, status: "Từ chối" } : item
    );
    setData(updated);
    message.warning(`Đơn #${id} đã bị từ chối.`);
  };

  return (
    <div className="medicine-table-container">
      <h2 className="medicine-title">Danh sách yêu cầu gửi thuốc</h2>
      <div className="medicine-table-wrapper">
        <table className="medicine-table">
          <thead>
            <tr>
              <th>Mã yêu cầu</th>
              <th>Ngày gửi</th>
              <th>Trạng thái</th>
              <th>Xem chi tiết</th>
              <th>Hành động</th>
            </tr>
          </thead>
          <tbody>
            {data.map((item) => (
              <tr key={item.requestId}>
                <td>{item.requestId}</td>
                <td>{item.date}</td>
                <td><span className="status-badge">{item.status}</span></td>
                <td>
                  <Button type="link" onClick={() => openDetail(item)}>
                    Xem chi tiết
                  </Button>
                </td>
                <td>
                  <Button
                    type="primary"
                    size="small"
                    style={{ marginRight: 6 }}
                    onClick={() => handleAccept(item.requestId)}
                  >
                    Chấp nhận
                  </Button>
                  <Button
                    type="default"
                    size="small"
                    danger
                    onClick={() => handleReject(item.requestId)}
                  >
                    Từ chối
                  </Button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>

      <Modal
        title="Chi tiết yêu cầu gửi thuốc"
        open={isModalVisible}
        onCancel={() => setIsModalVisible(false)}
        footer={null}
      >
        {selected && (
          <div className="detail-content">
            <p><strong>Mã đơn thuốc:</strong> {selected.requestId}</p>
            <p><strong>Tên đơn thuốc:</strong> {selected.requestName}</p>
            <p><strong>Ngày dùng:</strong> {selected.date}</p>

            <div style={{ display: 'flex', alignItems: 'center', gap: 10 }}>
              <strong>Trạng thái:</strong>
              <Select
                value={selected.status}
                onChange={handleStatusChange}
                style={{ width: 150 }}
                size="small"
              >
                {statusOptions.map(status => (
                  <Option key={status} value={status}>{status}</Option>
                ))}
              </Select>
            </div>

            <p><strong>Mã học sinh:</strong> {selected.studentDTO?.id}</p>
            <p><strong>Lớp:</strong> {selected.studentDTO?.classID}</p>
            <p><strong>Mục đích gửi thuốc:</strong> {selected.requestName}</p>
            <p><strong>Ghi chú:</strong> {selected.note || '-'}</p>
            <p><strong>Chi tiết đơn thuốc:</strong></p>
            <ul>
              {selected.medicalRequestDetailDTO?.map(detail => (
                <li key={detail.detailId}>
                  <strong>{detail.medicineName}</strong>: {detail.dosage} ({detail.time})
                </li>
              ))}
            </ul>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default MedicalRequest;
