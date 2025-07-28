import React, { useEffect, useState } from 'react';
import { Table, Tag, Space, Modal, Spin, Popconfirm } from 'antd';
import axios from 'axios';
import { max } from 'moment/moment';
import SendMedicineDetailModal from './SendMedicineDetailModal';
import Swal from 'sweetalert2';
// --- Sample Data ---
// In a real application, this data would come from an API call
const RequestTable = () => {
  const [data, setData] = useState([]);
  const [detailVisible, setDetailVisible] = useState(false);
  const [detailData, setDetailData] = useState(null);
  const [loadingDetail, setLoadingDetail] = useState(false);
  const [editingRequest, setEditingRequest] = useState(null);

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

  const handleEditRequest = (record) => {
    const time = record.medicalRequestDetailDTO?.[0]?.time || ''; // Lấy giá trị time từ medicalRequestDetailDTO
    window.dispatchEvent(new CustomEvent('edit-medicine-request', { detail: { ...record, time } }));
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
      render: (status) => {
        let color = "#888";
        let text = status;
        if (status === "PROCESSING") {
          color = "#ff9800";
          text = "Chờ duyệt";
        } else if (status === "SUBMITTED") {
          color = "#1976d2";
          text = "Đã duyệt";
        } else if (status === "COMPLETED") {
          color = "#4caf50";
          text = "Đã cho uống";
        } else if (status === "CANCELLED" || status === "CANCLE") {
          color = "#f44336";
          text = "Bị từ chối";
        }
        return (
          <span
            style={{
              background: color,
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
            {text}
          </span>
        );
      },
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
      align: 'center',
      render: (_, record) => (
        <div>
          {record.status === "PROCESSING" && (
            <a
              style={{ color: 'red', cursor: 'pointer' }}
              onClick={async () => {
                const result = await Swal.fire({
                  title: 'Bạn có chắc chắn muốn xóa đơn thuốc này?',
                  icon: 'warning',
                  showCancelButton: true,
                  confirmButtonText: 'Xóa',
                  cancelButtonText: 'Hủy',
                });
                if (result.isConfirmed) {
                  await handleDeleteRequest(record.requestId)();
                  Swal.fire('Đã xóa!', 'Đơn thuốc đã được xóa.', 'success');
                }
              }}
            >
              Xóa
            </a>
          )}
        </div>
      ),
    },
    {
      title: 'Chỉnh sửa',
      key: 'edit',
      align: 'center',
      render: (_, record) => (
        <div>
          {record.status === "PROCESSING" && (
            <a
              style={{ color: '#1976d2', cursor: 'pointer', marginLeft: 8 }}
              onClick={() => handleEditRequest(record)}
            >
              Chỉnh sửa
            </a>
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