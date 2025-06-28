import React, { useEffect, useState } from 'react';
import { Table, Tag, Space, Modal, Spin, Popconfirm } from 'antd';
import axios from 'axios';
import { max } from 'moment/moment';
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
        const res = await axios.get('http://localhost:8080/api/parent/medical-request', {
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
        `http://localhost:8080/api/parent/medical-request/by-request/${requestId}`,
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
      await axios.delete(`http://localhost:8080/api/parent/medical-request/${requestId}`, {
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
      align: 'center', // căn giữa
      render: (status) => (
        <span
          style={{
            background: status === "COMPLETED" ? "#4caf50" : "#888",
            color: "#fff",
            padding: "4px 16px",
            borderRadius: 6,
            fontWeight: "bold",
            fontSize: 14,
            letterSpacing: 1,
            display: "inline-block",
          }}
        >
          {status}
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
          {record.status !== "COMPLETED" && (
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
      <Modal
        open={detailVisible}
        onCancel={() => setDetailVisible(false)}
        footer={null}
        width={700}
        centered
        title={
          <div style={{ textAlign: "center", fontWeight: "bold", fontSize: 20 }}>
            Chi tiết đơn thuốc đã gửi
          </div>
        }
      >
        {loadingDetail ? (
          <Spin />
        ) : detailData ? (
          <div>
            <div style={{ marginBottom: 12 }}>
              <b>Mã đơn thuốc:</b> {detailData.requestId}
            </div>
            <div style={{ marginBottom: 12 }}>
              <b>Tên đơn thuốc:</b> {detailData.requestName}
            </div>
            <div style={{ marginBottom: 12 }}>
              <b>Ngày dùng:</b> {detailData.date}
            </div>
            <div style={{ marginBottom: 12 }}>
              <b>Ghi chú:</b> {detailData.note}
            </div>
            <div style={{ marginBottom: 12 }}>
              <b >Trạng thái:</b>{" "}
              <span
                style={{
                  background: detailData.status === "COMPLETED" ? "#4caf50" : "#888",
                  color: "#fff",
                  padding: "4px 16px",
                  borderRadius: 6,
                  fontWeight: "bold",
                  fontSize: 14,
                  letterSpacing: 1,
                  display: "inline-block",
                }}
              >
                {detailData.status}
              </span>
            </div>
            <div style={{ marginBottom: 12 }}>
              <b>Mã học sinh:</b> {detailData.studentId}
            </div>
            <div style={{ margin: "16px 0 8px", fontWeight: "bold"}}>Chi tiết đơn thuốc:</div>
            {detailData.medicalRequestDetailDTO?.map((item, idx) => (
              <div key={item.detailId || idx} style={{
                background: "#f6fbff",
                borderRadius: 8,
                padding: 12,
                marginBottom: 10
              }}>
                <div><b>Mục đích gửi thuốc:</b> {item.medicineName}</div>
                <div><b>Liều lượng:</b> {item.dosage}</div>
                <div><b>Thời gian:</b> {item.time}</div>
              </div>
            ))}
          </div>
        ) : (
          <div>Không có dữ liệu</div>
        )}
      </Modal>
    </div>
  );
};

export default RequestTable;