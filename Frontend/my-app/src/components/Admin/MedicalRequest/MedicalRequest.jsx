import React, { useEffect, useState } from "react";
import { Table, Button, Image, Tag, Modal, message, Input, Tabs } from "antd";
import axios from "axios";
import SendMedicineDetailModal from "./SendMedicineDetailModal";
import "./MedicalRequest.css";
import Swal from "sweetalert2";

const tabStatus = [
  { key: "ALL", label: "Tất cả" },
  { key: "PROCESSING", label: "Chờ duyệt" },
  { key: "CONFIRMED", label: "Đã duyệt" },
  { key: "COMPLETED", label: "Đã hoàn thành" },
  { key: "CANCELLED", label: "Từ chối" },
];

const MedicalRequest = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [activeTab, setActiveTab] = useState("ALL");

  // Lọc
  const [filterName, setFilterName] = useState("");
  const [filterStudent, setFilterStudent] = useState("");
  const [filterClass, setFilterClass] = useState("");

  const [detailData, setDetailData] = useState(null);
  const [detailLoading, setDetailLoading] = useState(false);

  const [rejectModalVisible, setRejectModalVisible] = useState(false);
  const [rejectDetailData, setRejectDetailData] = useState(null);
  const [rejectReason, setRejectReason] = useState("");
  const [rejectLoading, setRejectLoading] = useState(false);

  const [isRejectMode, setIsRejectMode] = useState(false);
  const [isGiveMedicineMode, setIsGiveMedicineMode] = useState(false);
  useEffect(() => {
    fetchRequests(activeTab);
    // eslint-disable-next-line
  }, [activeTab]);

  const fetchRequests = async (status) => {
    setLoading(true);
    try {
      const token = localStorage.getItem("token");
      let url =
        status === "ALL"
          ? "http://localhost:8080/api/nurse/medical-request"
          : `http://localhost:8080/api/nurse/medical-request/${status}`;
      const res = await axios.get(url, {
        headers: { Authorization: `Bearer ${token}` },
      });

      // Gộp các đơn có cùng requestId, gộp tất cả detail lại
      const map = new Map();
      for (const item of res.data) {
        if (!map.has(item.requestId)) {
          // Clone object và clone mảng chi tiết
          map.set(item.requestId, { ...item, medicalRequestDetailDTO: [...item.medicalRequestDetailDTO] });
        } else {
          // Đã có, chỉ cần push thêm các detail (không gộp theo timeSchedule, giữ nguyên từng detail)
          map.get(item.requestId).medicalRequestDetailDTO.push(...item.medicalRequestDetailDTO);
        }
      }
      setRequests(Array.from(map.values()));
    } catch {
      message.error("Không thể tải dữ liệu!");
      setRequests([]);
    } finally {
      setLoading(false);
    }
  };

  const handleApprove = async (id) => {
    const result = await Swal.fire({
      title: "Bạn chắc chắn muốn duyệt đơn thuốc này?",
      icon: "question",
      showCancelButton: true,
      confirmButtonText: "Duyệt",
      cancelButtonText: "Hủy",
    });

    if (result.isConfirmed) {
      const token = localStorage.getItem("token");
      try {
        await axios.put(
          `http://localhost:8080/api/nurse/medical-request/${id}/status`,
          { status: "CONFIRMED", reason_rejected: null },
          { headers: { Authorization: `Bearer ${token}` } }
        );
        message.success("Duyệt thành công!");
        fetchRequests(activeTab);
      } catch {
        message.error("Duyệt thất bại!");
      }
    }
  };

  const handleDelete = async (id) => {
    Modal.confirm({
      title: "Bạn chắc chắn muốn xóa đơn thuốc này?",
      okText: "Xóa",
      okType: "danger",
      cancelText: "Hủy",
      onOk: async () => {
        const token = localStorage.getItem("token");
        try {
          await axios.delete(`http://localhost:8080/api/nurse/medical-request/${id}`, {
            headers: { Authorization: `Bearer ${token}` },
          });
          message.success("Xóa thành công!");
          fetchRequests(activeTab);
        } catch {
          message.error("Xóa thất bại!");
        }
      },
    });
  };

const handleReject = (record) => {
  setDetailLoading(true);
  setIsRejectMode(true); // bật chế độ từ chối
  const found = requests.find(r => r.requestId === record.requestId);
  setDetailData([
    {
      ...found,
      medicalRequestDetailDTO: found?.medicalRequestDetailDTO || [],
      timeScheduleGroup: null,
    },
  ]);
  setModalVisible(true);
  setTimeout(() => setDetailLoading(false), 200);
  setRejectReason(""); // reset lý do mỗi lần mở
};

  const columns = [
    {
      title: "Mã đơn thuốc",
      dataIndex: "requestId",
      key: "requestId",
      align: "center",
      width: 30,
      render: (id) => <b>#{id}</b>,
    },
    {
      title: "Tên đơn thuốc",
      dataIndex: "requestName",
      key: "requestName",
      align: "center",
      width: 180,
    },
    {
      title: "Ngày cho uống",
      dataIndex: "date",
      key: "date",
      align: "center",
      width: 130,
      render: (date) =>
        date
          ? new Date(date).toLocaleDateString("vi-VN", {
              day: "2-digit",
              month: "2-digit",
              year: "numeric",
            })
          : "---",
    },
    {
      title: "Tên học sinh nhận thuốc",
      dataIndex: ["studentDTO", "fullName"],
      key: "studentName",
      align: "center",
      width: 180,
      render: (_, record) => record.studentDTO?.fullName || "---",
    },
    {
      title: "Lớp",
      dataIndex: ["studentDTO", "classDTO", "className"],
      key: "className",
      align: "center",
      width: 100,
      render: (_, record) => record.studentDTO?.classDTO?.className || "---",
    },
    {
      title: "Y tá phụ trách",
      dataIndex: ["nurseDTO", "fullName"],
      key: "nurseName",
      align: "center",
      width: 160,
      render: (_, record) =>
        record.nurseDTO?.fullName ? (
          <Tag color="blue">{record.nurseDTO.fullName}</Tag>
        ) : (
          <Tag color="default">Chưa có</Tag>
        ),
    },
    {
      title: "Ảnh",
      dataIndex: "image",
      key: "image",
      align: "center",
      width: 80,
      render: (img) =>
        img ? (
          <Image
            src={img}
            alt="Ảnh đơn thuốc"
            width={48}
            height={48}
            style={{ objectFit: "cover", borderRadius: 6, border: "1px solid #eee" }}
            preview={false}
          />
        ) : (
          <span>---</span>
        ),
    },
    {
      title: "Hành động",
      key: "actions",
      align: "center",
      width: 120,
      render: (_, record) => {
        // Lấy nurseId từ localStorage
        const nurseId = (() => {
          try {
            const userStr = localStorage.getItem("users");
            if (!userStr) return null;
            const user = JSON.parse(userStr);
            return user.id;
          } catch {
            return null;
          }
        })();

        return (
          <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 4 }}>
            <Button
              type="link"
              style={{ marginBottom: 4, padding: 0 }}
              onClick={() => {
              setDetailLoading(true);
              if (activeTab === "CONFIRMED") {
                setDetailData([
                  {
                    ...record,
                    medicalRequestDetailDTO: record.medicalRequestDetailDTO,
                    timeScheduleGroup: record.timeScheduleGroup,
                  },
                ]);
              } else {
                const found = requests.find(r => r.requestId === record.requestId);
                setDetailData([
                  {
                    ...found,
                    medicalRequestDetailDTO: found?.medicalRequestDetailDTO || [],
                    timeScheduleGroup: null,
                  },
                ]);
              }
              
              setModalVisible(true);
              setTimeout(() => setDetailLoading(false), 200);
            }}
          >
              Xem chi tiết
            </Button>
            {record.status === "CONFIRMED" ? (
              <Button
                type="primary"
                disabled={!record.nurseDTO || record.nurseDTO.id !== nurseId}
                
                onClick={() => {
                  if (record.nurseDTO && record.nurseDTO.id === nurseId) {
                    setDetailLoading(true);
                    setIsGiveMedicineMode(true);
                    setDetailData([
                      {
                        ...record,
                        medicalRequestDetailDTO: record.medicalRequestDetailDTO,
                        timeScheduleGroup: record.timeScheduleGroup,
                      },
                    ]);
                    setIsGiveMedicineMode(true);
                    setModalVisible(true);
                    setTimeout(() => setDetailLoading(false), 200);
                  }
                }}
              >
                Ghi nhận
              </Button>
            ) : (
              <div>
                <Button
                  type="primary"
                  style={{ marginRight: 8 }}
                  disabled={record.status !== "PROCESSING"}
                  onClick={() => handleApprove(record.requestId)}
                >
                  Duyệt
                </Button>
                <Button
                  danger
                  onClick={() => {
                    handleReject(record);
                  }}
                  disabled={record.status !== "PROCESSING"}
                >
                  Từ chối
                </Button>
              </div>
            )}
          </div>
        );
      },
    },
  ];

  // Tạo mảng dữ liệu cho table
  let tableData = [];
  if (activeTab === "CONFIRMED") {
    // Tab "Đã duyệt": tách dòng theo từng khung giờ
    requests.forEach((req) => {
      const group = {};
      req.medicalRequestDetailDTO.forEach((detail) => {
        const key = detail.timeSchedule || "Khác";
        if (!group[key]) group[key] = [];
        group[key].push(detail);
      });
      Object.entries(group).forEach(([time, details]) => {
        tableData.push({
          ...req,
          medicalRequestDetailDTO: details,
          timeScheduleGroup: time,
          rowKey: req.requestId + "_" + time,
        });
      });
    });
  } else {
    // Các tab khác: mỗi đơn là 1 dòng, không tách theo khung giờ
    tableData = requests.map((req) => ({
      ...req,
      timeScheduleGroup: null,
      rowKey: req.requestId,
    }));
  }

  // Lọc dữ liệu theo 3 trường filter
  const filteredData = tableData
    .filter((item) =>
      item.requestName?.toLowerCase().includes(filterName.toLowerCase())
    )
    .filter((item) =>
      item.studentDTO?.fullName?.toLowerCase().includes(filterStudent.toLowerCase())
    )
    .filter((item) =>
      item.studentDTO?.classDTO?.className?.toLowerCase().includes(filterClass.toLowerCase())
    );

  return (
    <div className="medical-request-wrapper">
      <div className="medicine-table-container">
        <h2 style={{ marginBottom: 16 }}>Quản lý đơn thuốc</h2>
        {/* 3 ô lọc */}
        <div style={{ marginBottom: 16, display: "flex", gap: 12 }}>
          <Input
            placeholder="Lọc theo tên đơn thuốc"
            value={filterName}
            onChange={e => setFilterName(e.target.value)}
            style={{ width: 220 }}
            allowClear
          />
          <Input
            placeholder="Lọc theo tên học sinh"
            value={filterStudent}
            onChange={e => setFilterStudent(e.target.value)}
            style={{ width: 220 }}
            allowClear
          />
          <Input
            placeholder="Lọc theo lớp"
            value={filterClass}
            onChange={e => setFilterClass(e.target.value)}
            style={{ width: 180 }}
            allowClear
          />
        </div>
        <Tabs
          activeKey={activeTab}
          onChange={setActiveTab}
          items={tabStatus.map(tab => ({
            key: tab.key,
            label: tab.label,
          }))}
          style={{ marginBottom: 16 }}
        />
        <Table
          columns={columns}
          dataSource={filteredData}
          rowKey="rowKey"
          loading={loading}
          pagination={{ pageSize: 9 }}
          bordered
          scroll={{ x: 1100 }}
        />
<SendMedicineDetailModal
  open={modalVisible}
  onClose={() => {
    setModalVisible(false);
    setIsRejectMode(false);
    setIsGiveMedicineMode(false);
  }}
  loading={detailLoading}
  detailData={detailData}
  reason={rejectReason}
  onReasonChange={e => setRejectReason(e.target.value)}
  isRejectMode={isRejectMode}
  isGiveMedicineMode={isGiveMedicineMode}
  showReasonInput={isRejectMode}
  onRejectSuccess={() => {
    setModalVisible(false);
    setIsRejectMode(false);
    fetchRequests(activeTab);
  }}
  onGiveMedicine={() => {
    setModalVisible(false);
    setIsGiveMedicineMode(false);
    fetchRequests(activeTab); // fetch lại dữ liệu sau khi ghi nhận
  }}
/>
        <Modal
          title="Lý do từ chối đơn thuốc"
          visible={rejectModalVisible}
          onCancel={() => setRejectModalVisible(false)}
          footer={null}
          width={400}
        >
          <div style={{ marginBottom: 16 }}>
            <b>Mã đơn thuốc:</b> #{rejectDetailData?.requestId}
          </div>
          <div style={{ marginBottom: 16 }}>
            <b>Tên đơn thuốc:</b> {rejectDetailData?.requestName}
          </div>
          <div style={{ marginBottom: 16 }}>
            <b>Ngày cho uống:</b>{" "}
            {rejectDetailData?.date
              ? new Date(rejectDetailData.date).toLocaleDateString("vi-VN", {
                  day: "2-digit",
                  month: "2-digit",
                  year: "numeric",
                })
              : "---"}
          </div>
          <div style={{ marginBottom: 16 }}>
            <b>Tên học sinh:</b> {rejectDetailData?.studentDTO?.fullName}
          </div>
          <div style={{ marginBottom: 16 }}>
            <b>Lớp:</b> {rejectDetailData?.studentDTO?.classDTO?.className}
          </div>
          <div style={{ marginBottom: 16 }}>
            <b>Y tá phụ trách:</b> {rejectDetailData?.nurseDTO?.fullName}
          </div>
          <div style={{ marginBottom: 16 }}>
            <b>Ảnh đơn thuốc:</b>
            <div style={{ marginTop: 8 }}>
              {rejectDetailData?.image ? (
                <Image
                  src={rejectDetailData.image}
                  alt="Ảnh đơn thuốc"
                  width={100}
                  height={100}
                  style={{ objectFit: "cover", borderRadius: 8, border: "1px solid #eee" }}
                  preview={false}
                />
              ) : (
                <span>---</span>
              )}
            </div>
          </div>
          <div style={{ marginBottom: 16 }}>
            <b>Lý do từ chối:</b>
            <Input.TextArea
              rows={4}
              value={rejectReason}
              onChange={e => setRejectReason(e.target.value)}
              placeholder="Nhập lý do từ chối đơn thuốc"
              style={{ marginTop: 8 }}
            />
          </div>
          <div style={{ display: "flex", justifyContent: "flex-end", gap: 8 }}>
            <Button onClick={() => setRejectModalVisible(false)}>
              Hủy
            </Button>
            <Button
              type="primary"
              danger
              loading={rejectLoading}
              onClick={() => {
                setRejectLoading(true);
                handleReject(rejectDetailData.requestId);
              }}
            >
              Từ chối đơn thuốc
            </Button>
          </div>
        </Modal>
      </div>
    </div>
  );
};

export default MedicalRequest;

