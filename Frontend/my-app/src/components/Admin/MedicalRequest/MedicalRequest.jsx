import React, { useEffect, useState } from "react";
import { Table, Button, Image, Tag, Modal, message, Input, Tabs, DatePicker, Select } from "antd";
import axios from "axios";
import SendMedicineDetailModal from "./SendMedicineDetailModal";
import "./MedicalRequest.css";
import Swal from "sweetalert2";
import dayjs from "dayjs";
import isSameOrAfter from "dayjs/plugin/isSameOrAfter";
import isSameOrBefore from "dayjs/plugin/isSameOrBefore";
dayjs.extend(isSameOrAfter);
dayjs.extend(isSameOrBefore);

const tabStatus = [
  { key: "PROCESSING", label: "Chờ duyệt" },
  { key: "CONFIRMED", label: "Đã duyệt" },
  { key: "COMPLETED", label: "Đã hoàn thành" },
  { key: "CANCELLED", label: "Từ chối" },
  { key: "ALL", label: "Tất cả" },
];

const MedicalRequest = () => {
  const [requests, setRequests] = useState([]);
  const [loading, setLoading] = useState(false);
  const [modalVisible, setModalVisible] = useState(false);
  const [selectedId, setSelectedId] = useState(null);
  const [activeTab, setActiveTab] = useState("PROCESSING");

  // Lọc
  const [filterName, setFilterName] = useState("");
  const [filterStudent, setFilterStudent] = useState("");
  const [filterClass, setFilterClass] = useState("");
  const [filterFromDate, setFilterFromDate] = useState(null);
  const [filterToDate, setFilterToDate] = useState(null);
  const [filterNurse, setFilterNurse] = useState("");
  const [filterTime, setFilterTime] = useState("");

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
      <div style={{ display: "flex", flexDirection: "column", alignItems: "center" }}>
        <div style={{ fontWeight: 600, color: "#1890ff" }}>
          {record.nurseDTO.fullName}
        </div>
        <div style={{ fontSize: 13, color: "#888" }}>
          SĐT: {record.nurseDTO.phone}
        </div>
      </div>
    ) : (
      <span style={{ color: "#bbb" }}>Chưa có</span>
    ),
},
{
      title: "Khung giờ cho uống",
      key: "timeSchedules",
      align: "center",
      width: 180,
      render: (_, record) => {
        if (!record.medicalRequestDetailDTO || record.medicalRequestDetailDTO.length === 0) return "---";
        // Lấy tất cả timeSchedule, loại trùng
        const uniqueTimes = [
          ...new Set(
            record.medicalRequestDetailDTO
              .map(item => item.timeSchedule)
              .filter(Boolean)
          ),
        ];
        return uniqueTimes.length > 0 ? (
  <span style={{ whiteSpace: "pre-line" }}>
    {uniqueTimes.join("\n")}
  </span>
) : "---";
      },
    },
    {
      title: "Trạng thái",
      dataIndex: "status",
      key: "status",
      align: "center",
      width: 120,
      render: (status) => {
        let color = "#faad14";
        let text = "Chờ duyệt";
        if (status === "CONFIRMED") {
          color = "#1890ff";
          text = "Đã xác nhận";
        } else if (status === "COMPLETED") {
          color = "#21ba45";
          text = "Hoàn thành";
        } else if (status === "CANCELLED") {
          color = "#ff4d4f";
          text = "Bị từ chối";
        }
        return (
          <span style={{ fontWeight: 600, color }}>
            {text}
          </span>
        );
      },
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

        const userRole = (() => {
  try {
    const userStr = localStorage.getItem("users");
    if (!userStr) return null;
    const user = JSON.parse(userStr);
    return user.role;
  } catch {
    return null;
  }
})();

        if (record.status === "COMPLETED") {
          return (
            <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 4 }}>
              <Button
                type="link"
                style={{ marginBottom: 4, padding: 0 }}
                onClick={() => {
                  setDetailLoading(true);
                  setDetailData([
                    {
                      ...record,
                      medicalRequestDetailDTO: record.medicalRequestDetailDTO,
                      timeScheduleGroup: record.timeScheduleGroup,
                    },
                  ]);
                  setModalVisible(true);
                  setTimeout(() => setDetailLoading(false), 200);
                }}
              >
                Xem chi tiết
              </Button>
              
              <Button
                type="primary"
                style={{ background: "#21ba45", borderColor: "#21ba45", cursor: "default" }}
                disabled
              >
                Đã cho uống
              </Button>
              
            </div>
          );
        }

        // Nếu bị từ chối, chỉ hiện nút Xem chi tiết
        if (record.status === "CANCELLED") {
          return (
            <div style={{ display: "flex", flexDirection: "column", alignItems: "center", gap: 4 }}>
              <Button
                type="link"
                style={{ marginBottom: 4, padding: 0 }}
                onClick={() => {
                  setDetailLoading(true);
                  setDetailData([
                    {
                      ...record,
                      medicalRequestDetailDTO: record.medicalRequestDetailDTO,
                      timeScheduleGroup: record.timeScheduleGroup,
                    },
                  ]);
                  setModalVisible(true);
                  setTimeout(() => setDetailLoading(false), 200);
                }}
              >
                Xem chi tiết
              </Button>
              
              <Button
                type="primary"
                style={{ background: "#ff3c00ff", color: "black", borderColor: "#ff0000ff", cursor: "default" }}
                disabled
              >
                Đã từ chối
              </Button>
              
            </div>
          );
        }

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
                disabled={userRole === "ADMIN" || !record.nurseDTO || record.nurseDTO.id !== nurseId}
                onClick={() => {
                  if (userRole !== "ADMIN" && record.nurseDTO && record.nurseDTO.id === nurseId) {
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
                  disabled={userRole === "ADMIN" || record.status !== "PROCESSING"}
                  onClick={() => handleApprove(record.requestId)}
                >
                  Duyệt
                </Button>
                <Button
                  danger
                  onClick={() => {
                    handleReject(record);
                  }}
                  disabled={userRole === "ADMIN" || record.status !== "PROCESSING"}
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
    // Sắp xếp giảm dần theo requestId
    tableData.sort((a, b) => b.requestId - a.requestId);
  } else if (
    activeTab === "PROCESSING" ||
    activeTab === "COMPLETED" ||
    activeTab === "CANCELLED"
  ) {
    tableData = requests
      .map((req) => ({
        ...req,
        timeScheduleGroup: null,
        rowKey: req.requestId,
      }))
      .sort((a, b) => b.requestId - a.requestId);
  } else if (activeTab === "ALL") {
    // Ưu tiên PROCESSING, rồi đến các trạng thái khác, cùng trạng thái thì giảm dần requestId
    const statusPriority = {
      PROCESSING: 1,
      CONFIRMED: 2,
      COMPLETED: 3,
      CANCELLED: 4,
    };
    tableData = requests
      .map((req) => ({
        ...req,
        timeScheduleGroup: null,
        rowKey: req.requestId,
      }))
      .sort((a, b) => {
        const pa = statusPriority[a.status] || 99;
        const pb = statusPriority[b.status] || 99;
        if (pa !== pb) return pa - pb;
        return b.requestId - a.requestId;
      });
  }

  // Lọc dữ liệu theo 3 trường filter
  const filteredData = tableData
  .filter(item =>
    item.requestName?.toLowerCase().includes(filterName.toLowerCase())
  )
  .filter(item =>
    item.studentDTO?.fullName?.toLowerCase().includes(filterStudent.toLowerCase())
  )
  .filter(item =>
    item.studentDTO?.classDTO?.className?.toLowerCase().includes(filterClass.toLowerCase())
  )
  .filter(item => {
    if (!filterFromDate) return true;
    const date = dayjs(item.date);
    return date.isSameOrAfter(filterFromDate, "day");
  })
  .filter(item => {
    if (!filterToDate) return true;
    const date = dayjs(item.date);
    return date.isSameOrBefore(filterToDate, "day");
  })
  .filter(item => {
    if (!filterNurse) return true;
    return item.nurseDTO?.fullName === filterNurse;
  })
  .filter(item => {
    if (!filterTime) return true;
    return (item.medicalRequestDetailDTO || []).some(d => d.timeSchedule === filterTime);
  });

  return (
    <div className="medical-request-wrapper">
      <div className="medicine-table-container">
        <div style={{ display: "flex", justifyContent: "space-between", alignItems: "center", marginBottom: 16 }}>
          <h2 style={{ margin: 0 }}>Quản lý đơn thuốc</h2>
          <Button
            type="primary"
            onClick={() => {
              // TODO: Thay bằng navigation hoặc mở modal tạo đơn thuốc tùy theo thiết kế của bạn
              message.info("Chức năng tạo đơn thuốc!");
            }}
          >
            Tạo đơn thuốc
          </Button>
        </div>
        {/* 3 ô lọc */}
        <div style={{ marginBottom: 16, display: "flex", gap: 12, flexWrap: "wrap", alignItems: "center" }}>
          <Input
            placeholder="Lọc theo tên đơn thuốc"
            value={filterName}
            onChange={e => setFilterName(e.target.value)}
            style={{ width: 180 }}
            allowClear
          />
          <Input
            placeholder="Lọc theo tên học sinh"
            value={filterStudent}
            onChange={e => setFilterStudent(e.target.value)}
            style={{ width: 180 }}
            allowClear
          />
          <Input
            placeholder="Lọc theo lớp"
            value={filterClass}
            onChange={e => setFilterClass(e.target.value)}
            style={{ width: 120 }}
            allowClear
          />
          <DatePicker
  placeholder="Từ ngày"
  value={filterFromDate}
  onChange={setFilterFromDate}
  style={{ width: 120 }}
  allowClear
  format="DD/MM/YYYY"
/>
<DatePicker
  placeholder="Đến ngày"
  value={filterToDate}
  onChange={setFilterToDate}
  style={{ width: 120 }}
  allowClear
  format="DD/MM/YYYY"
/>
<Select
  placeholder="Y tá phụ trách"
  allowClear
  style={{ width: 160 }}
  value={filterNurse || undefined}
  onChange={setFilterNurse}
  options={[
    ...Array.from(new Set(requests.map(r => r.nurseDTO?.fullName).filter(Boolean))).map(name => ({
      value: name,
      label: name,
    }))
  ]}
/>
<Select
  placeholder="Khung giờ"
  allowClear
  style={{ width: 180 }}
  value={filterTime || undefined}
  onChange={setFilterTime}
  options={[
    ...Array.from(new Set(requests.flatMap(r => r.medicalRequestDetailDTO?.map(d => d.timeSchedule)).filter(Boolean))).map(time => ({
      value: time,
      label: time,
    }))
  ]}
/>
<Button
    onClick={() => {
      setFilterName("");
      setFilterStudent("");
      setFilterClass("");
      setFilterFromDate(null);
      setFilterToDate(null);
      setFilterNurse("");
      setFilterTime("");
    }}
    style={{ minWidth: 140 }}
  >
    Xóa các phần đã chọn
  </Button>
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
      </div>
    </div>
  );
};

export default MedicalRequest;

