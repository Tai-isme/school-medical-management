import React, { useEffect, useState } from 'react';
import { Table, Tag, Space, Modal, Spin, Popconfirm } from 'antd';
import axios from 'axios';
import { max } from 'moment/moment';
import SendMedicineDetailModal from './SendMedicineDetailModal';
// --- Sample Data ---
// In a real application, this data would come from an API call
const RequestTable = () => {
  const [data, setData] = useState([]);
  const [detailVisible, setDetailVisible] = useState(false);
  const [detailData, setDetailData] = useState(null);
  const [loadingDetail, setLoadingDetail] = useState(false);

  useEffect(() => {
    const fetchRequests = async () => {
      try {
        const token = localStorage.getItem('token'); // nếu cần token
        const res = await axios.get('http://localhost:8080/api/parent/medical-request', { // LẤY TẤT CẢ ĐƠN THUỐC
          headers: { Authorization: `Bearer ${token}` },
        });
        setData(res.data);
      } catch (err) {
        setData([]);
      }
    };
    fetchRequests();
  }, []);

  const handleShowDetail = async (requestId) => {
    setLoadingDetail(true);
    setDetailVisible(true);
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get(
        `http://localhost:8080/api/parent/medical-request/by-request/${requestId}`, // XEM CHI TIẾT
        { headers: { Authorization: `Bearer ${token}` } }
      );
      setDetailData(res.data);
    } catch (err) {
      setDetailData(null);
    }
    setLoadingDetail(false);
  };

  const handleDeleteRequest = (requestId) => async () => {
    const token = localStorage.getItem('token'); // nếu cần token
    console.log('Deleting request with ID:', requestId);
    try {
      await axios.delete(`http://localhost:8080/api/parent/medical-request/${requestId}`, { //DELETE request
        headers: { Authorization: `Bearer ${token}` },
      });
      setData((prevData) => prevData.filter((item) => item.requestId !== requestId));
    } catch (err) {
      console.error('Error deleting request:', err);
    }
  };

  // --- Column Definitions ---
  const columns = [
    {
      title: 'Mã đơn thuốc',
      dataIndex: 'requestId',
      key: 'requestId',
      sorter: (a, b) => a.requestId - b.requestId,
      align: 'center', // căn giữa
    },
    {
      title: 'Mục đích gửi thuốc',
      dataIndex: 'requestName',
      key: 'requestName',
      sorter: (a, b) => a.requestName.localeCompare(b.requestName),
      
      // Không căn giữa
    },
    {
      title: 'Ngày gửi',
      dataIndex: 'date',
      key: 'date',
      sorter: (a, b) => new Date(a.date) - new Date(b.date),
      align: 'center', // căn giữa
    },
    {
      title: "Trạng thái",
      dataIndex: "status",
      key: "status",
      align: 'center',
      render: (status) => (
        <span
          style={{
            background:
              status === "COMPLETED"
                ? "#4caf50"
                : status === "SUBMITTED"
                ? "#1976d2"
                : status === "PROCESSING"
                ? "#ff9800"
                : status === "CANCELLED"
                ? "#f44336"
                : "#888",
            color: "#fff",
            padding: "4px 0",
            borderRadius: 6,
            fontWeight: "bold",
            fontSize: 14,
            letterSpacing: 1,
            display: "inline-block",
            minWidth: 130,
            textAlign: "center"
          }}
        >
          {status === "COMPLETED"
            ? "Hoàn thành"
            : status === "SUBMITTED"
            ? "Chờ duyệt"
            : status === "PROCESSING"
            ? "Đang xử lý"
            : status === "CANCLE" || status === "CANCELLED"
            ? "Bị từ chối"
            : status}
        </span>
      ),
    },
    {
      title: 'Xem chi tiết đơn thuốc',
      key: 'detail',
      minWidth: 200,
      align: 'center', // căn giữa
      render: (_, record) => (
        <a
          onClick={() => handleShowDetail(record.requestId)}
          style={{ color: '#1976d2', cursor: 'pointer' }}
        >
          Nhấn để xem
        </a>
      ),
    },
    {
      title: 'Xóa đơn thuốc',
      key: 'action',
      align: 'center', // căn giữa
      render: (_, record) => (
        <div>
          {record.status !== "COMPLETED" &&
            record.status !== "PROCESSING" &&
            record.status !== "CANCELLED" && (
              <Popconfirm
                title="Bạn có chắc chắn muốn xóa đơn thuốc này?"
                onConfirm={handleDeleteRequest(record.requestId)}
                okText="Xóa"
                cancelText="Hủy"
              >
                <a style={{ color: 'red', cursor: 'pointer' }}>Xóa</a>
              </Popconfirm>
            )}
        </div>
      ),
    },
  ];

  // --- The React Component ---

  return (
    <div style={{ padding: '24px' }}>
      <h1>Lịch sử gửi thuốc</h1>
      <Table
        columns={columns}
        dataSource={data}
        rowKey="requestId" // It's crucial to provide a unique key for each row
        pagination={{ pageSize: 5 }}
      />
      <SendMedicineDetailModal
        open={detailVisible}
        onClose={() => setDetailVisible(false)}
        loading={loadingDetail}
        detailData={detailData}
      />
    </div>
  );
};

export default RequestTable;