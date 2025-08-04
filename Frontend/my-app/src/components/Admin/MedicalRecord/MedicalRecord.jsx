import React, { useEffect, useState } from "react";
import { Table, Tag, Spin, Alert } from "antd";
import moment from "moment";
import axios from "axios";
import "./MedicalRecord.css";
import { urlServer } from "../../../api/urlServer";
export default function MedicalRecordTable({ selectedStudentId }) {
  const [medicalRecord, setMedicalRecord] = useState(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState(null);

  useEffect(() => {
    const fetchMedicalRecord = async () => {
      if (!selectedStudentId) {
        setMedicalRecord(null);
        setLoading(false);
        setError(null);
        return;
      }

      setLoading(true);
      setError(null);
      const token = localStorage.getItem("token");

      if (!token) {
        setError("Không tìm thấy token xác thực. Vui lòng đăng nhập lại.");
        setLoading(false);
        return;
      }

      try {
        const response = await axios.get(
          `${urlServer}/api/admin/medical-records/${selectedStudentId}`,
          {
            headers: {
              Authorization: `Bearer ${token}`,
              "Content-Type": "application/json",
            },
          }
        );
        setMedicalRecord(response.data);
      } catch (err) {
        console.error("Lỗi khi tải hồ sơ y tế:", err);
        setError(
          err.response?.data?.message ||
            err.message ||
            "Đã xảy ra lỗi khi tải hồ sơ y tế."
        );
      } finally {
        setLoading(false);
      }
    };

    fetchMedicalRecord();
  }, [selectedStudentId]);

  const columns = [
    { title: "Mã hồ sơ", dataIndex: "recordId", key: "recordId", width: 100 },
    {
      title: "Mã học sinh",
      dataIndex: "studentId",
      key: "studentId",
      width: 100,
    },
    { title: "Dị ứng", dataIndex: "allergies", key: "allergies", width: 120 },
    {
      title: "Bệnh mãn tính",
      dataIndex: "chronicDisease",
      key: "chronicDisease",
      width: 120,
    },
    {
      title: "Tiền sử điều trị",
      dataIndex: "treatmentHistory",
      key: "treatmentHistory",
      width: 150,
    },
    { title: "Thị lực", dataIndex: "vision", key: "vision", width: 80 },
    { title: "Thính lực", dataIndex: "hearing", key: "hearing", width: 80 },
    { title: "Cân nặng (kg)", dataIndex: "weight", key: "weight", width: 100 },
    { title: "Chiều cao (cm)", dataIndex: "height", key: "height", width: 100 },
    {
      title: "Ngày cập nhật",
      dataIndex: "lastUpdate",
      key: "lastUpdate",
      width: 140,
      render: (text) =>
        text ? moment(text).format("DD/MM/YYYY HH:mm") : "N/A",
    },
    { title: "Ghi chú", dataIndex: "note", key: "note", width: 120 },
    {
      title: "Lịch sử tiêm chủng",
      dataIndex: "vaccineHistories",
      key: "vaccineHistories",
      width: 200,
      render: (vaccineHistories) =>
        vaccineHistories &&
        Array.isArray(vaccineHistories) &&
        vaccineHistories.length > 0 ? (
          vaccineHistories.map((vaccine) => (
            <Tag key={vaccine.id} color="blue" className="medical-tag">
              {vaccine.vaccineName}
              {vaccine.note ? ` (${vaccine.note})` : ""}
            </Tag>
          ))
        ) : (
          <span>Không có</span>
        ),
    },
  ];

  if (loading) {
    return (
      <div className="medical-loading">
        <Spin size="large" tip="Đang tải hồ sơ y tế..." />
      </div>
    );
  }

  if (error) {
    return (
      <Alert
        message="Lỗi"
        description={error}
        type="error"
        showIcon
        className="medical-alert"
      />
    );
  }

  if (!medicalRecord) {
    return (
      <Alert
        message="Thông báo"
        description="Vui lòng chọn một học sinh để xem hồ sơ y tế."
        type="info"
        showIcon
        className="medical-alert"
      />
    );
  }

  return (
    <div className="medical-table-container">
      <Table
        columns={columns}
        dataSource={[medicalRecord]}
        rowKey="recordId"
        scroll={{ x: 1200 }}
        pagination={false}
        bordered
      />
    </div>
  );
}
