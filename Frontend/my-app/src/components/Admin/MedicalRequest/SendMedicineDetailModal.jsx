import React, { useState, useEffect } from "react";
import { Modal, Spin, Row, Col, Image, Input, Button, message } from "antd";
import axios from "axios";
import Swal from "sweetalert2";
import { urlServer } from "../../../api/urlServer"; // Import the URL server from your config
const SendMedicineDetailModal = ({
  open,
  onClose,
  loading,
  detailData,
  reason,
  onReasonChange,
  showReasonInput = false, // mặc định không hiện ô nhập lý do
  onRejectSuccess, // callback khi từ chối thành công
  isRejectMode,
  isGiveMedicineMode, // truyền prop này từ cha khi nurse bấm "Đã cho uống"
  onGiveMedicine, // callback để lưu kết quả
}) => {
  const [confirmLoading, setConfirmLoading] = useState(false);
  const [medicineNotes, setMedicineNotes] = useState({});
  // Thêm state lưu trạng thái uống thuốc cho từng detail
  const [medicineStatus, setMedicineStatus] = useState({});
  const [showNurseInfo, setShowNurseInfo] = useState({});

  console.log("isGiveMedicineMode:", isGiveMedicineMode);

  // Reset ghi chú khi mở modal mới
  useEffect(() => {
    if (detailData && detailData[0]?.medicalRequestDetailDTO) {
      const notes = {};
      detailData[0].medicalRequestDetailDTO.forEach((item) => {
        notes[item.detailId] = item.note || "";
      });
      setMedicineNotes(notes);
    }
  }, [detailData, open]);

  // Reset trạng thái uống thuốc khi mở modal mới
  useEffect(() => {
    if (detailData && detailData[0]?.medicalRequestDetailDTO) {
      const statusObj = {};
      detailData[0].medicalRequestDetailDTO.forEach((item) => {
        statusObj[item.detailId] = item.status === "TAKEN";
      });
      setMedicineStatus(statusObj);
    }
  }, [detailData, open]);

  // Hàm xử lý thay đổi ghi chú
  const handleNoteChange = (detailId, value) => {
    setMedicineNotes((prev) => ({
      ...prev,
      [detailId]: value,
    }));
  };

  // Hàm xử lý tick
  const handleStatusChange = (detailId, value) => {
    setMedicineStatus((prev) => ({
      ...prev,
      [detailId]: value,
    }));
  };

  // Hàm xác nhận cho uống thuốc
  const handleGiveMedicine = async () => {
    setConfirmLoading(true);
    try {
      // Chuẩn bị dữ liệu gửi lên API
      const data = Object.entries(medicineNotes).map(
        ([requestDetailId, note]) => ({
          requestDetailId: Number(requestDetailId),
          note: note || "",
          status: medicineStatus[requestDetailId] ? "TAKEN" : "NOT_TAKEN",
        })
      );

      const token = localStorage.getItem("token");
      await axios.put(
        `${urlServer}/api/nurse/medical-request-detail/status/taken`,
        data,
        {
          headers: {
            Authorization: `Bearer ${token}`,
            "Content-Type": "application/json",
          },
        }
      );

      message.success("Đã lưu ghi chú và xác nhận cho uống thuốc!");
      if (onGiveMedicine) onGiveMedicine(medicineNotes); // callback ở MedicalRequest
      if (window && window.location) {
        // Không reload trang, chỉ đóng modal
        if (onClose) onClose();
      }
    } catch {
      message.error("Lưu thất bại!");
    } finally {
      setConfirmLoading(false);
    }
  };

  // Lấy requestId từ detailData
  const requestId =
    Array.isArray(detailData) && detailData.length > 0
      ? detailData[0].requestId
      : null;

  const handleReject = async () => {
    if (!reason || !reason.trim()) {
      await Swal.fire({
        icon: "warning",
        title: "Vui lòng nhập lý do từ chối!",
        confirmButtonText: "OK",
      });
      return;
    }

    const result = await Swal.fire({
      title: "Bạn chắc chắn muốn từ chối đơn thuốc này?",
      text: "Hành động này sẽ không thể hoàn tác.",
      icon: "warning",
      showCancelButton: true,
      confirmButtonText: "Từ chối",
      cancelButtonText: "Hủy",
      confirmButtonColor: "#d33",
    });

    if (result.isConfirmed) {
      setConfirmLoading(true);
      try {
        const token = localStorage.getItem("token");
        await axios.put(
          `${urlServer}/api/nurse/medical-request/${requestId}/status`,
          { status: "CANCELLED", reason_rejected: reason },
          { headers: { Authorization: `Bearer ${token}` } }
        );
        message.success("Đã từ chối đơn thuốc!");
        if (onRejectSuccess) onRejectSuccess();
        if (onClose) onClose();
      } catch {
        message.error("Từ chối thất bại!");
      } finally {
        setConfirmLoading(false);
      }
    }
  };
  const statusMap = {
    CONFIRMED: "ĐÃ XÁC NHẬN",
    COMPLETED: "HOÀN THÀNH",
    PROCESSING: "CHỜ XÁC NHẬN",
    CANCELLED: "ĐÃ BỊ HỦY",
  };

  const statusColorMap = {
    CONFIRMED: "#1976d2", // xanh dương
    COMPLETED: "green", // xanh lá
    PROCESSING: "orange", // cam
    CANCELLED: "red", // đỏ
  };

  const toggleNurseInfo = (requestId) => {
    setShowNurseInfo((prev) => ({
      ...prev,
      [requestId]: !prev[requestId],
    }));
  };
  return (
    <Modal
      open={open}
      onCancel={onClose}
      footer={null}
      width={900}
      centered
      title={
        <div style={{ textAlign: "center", fontWeight: "bold", fontSize: 20 }}>
          Chi tiết đơn thuốc đã gửi
        </div>
      }
    >
      {loading ? (
        <Spin />
      ) : Array.isArray(detailData) && detailData.length > 0 ? (
        <>
          {detailData.map((group, idx) => (
            <Row gutter={32} key={idx} style={{ marginBottom: 24 }}>
              <Col xs={24} md={12}>
                <div
                  style={{
                    background: "#f6fbff",
                    borderRadius: 12,
                    padding: 24,
                    height: "100%",
                  }}
                >
                  <div style={{ marginBottom: 12 }}>
                    <b>Mã đơn thuốc:</b> {group.requestId}
                  </div>
                  <div style={{ marginBottom: 12 }}>
                    <b>Tên đơn thuốc:</b> {group.requestName}
                  </div>
                  <div style={{ marginBottom: 12 }}>
                    <b>Ngày dùng:</b>{" "}
                    {group.date
                      ? new Date(group.date).toLocaleDateString("vi-VN", {
                          day: "2-digit",
                          month: "2-digit",
                          year: "numeric",
                        })
                      : "---"}
                  </div>
                  <div style={{ marginBottom: 12 }}>
                    <b>Ghi chú:</b> {group.note}
                  </div>
                  <div style={{ marginBottom: 12 }}>
                    <b>Trạng thái:</b>{" "}
                    <span
                      style={{
                        color: statusColorMap[group.status] || "gray",
                        fontWeight: "bold",
                      }}
                    >
                      {statusMap[group.status] || "KHÔNG XÁC ĐỊNH"}
                    </span>
                  </div>

                  <div style={{ marginBottom: 12 }}>
                    <b>Học sinh nhận thuốc:</b> {group.studentDTO.fullName}
                  </div>
                  <div style={{ marginBottom: 12 }}>
                    <b>Y tá phụ trách:</b>{" "}
                    {group.nurseDTO && group.nurseDTO.fullName ? (
                      <span>
                        <span
                          style={{
                            color: "#1976d2",
                            cursor: "pointer",
                            fontWeight: "bold",
                          }}
                          onClick={() => toggleNurseInfo(group.requestId)}
                        >
                          {group.nurseDTO.fullName}
                        </span>
                        {showNurseInfo[group.requestId] && (
                          <div style={{ marginTop: 8, paddingLeft: 12 }}>
                            <div>
                              <b>SĐT:</b> {group.nurseDTO.phone}
                            </div>
                            <div>
                              <b>Email:</b> {group.nurseDTO.email}
                            </div>
                          </div>
                        )}
                      </span>
                    ) : (
                      "Chưa có"
                    )}
                  </div>
                  {group.image && (
                    <div style={{ marginBottom: 16 }}>
                      <b>Ảnh đơn thuốc:</b>
                      <br />
                      <Image
                        src={group.image}
                        alt="Ảnh đơn thuốc"
                        style={{
                          maxWidth: 220,
                          maxHeight: 220,
                          borderRadius: 8,
                          border: "1px solid #eee",
                          marginTop: 8,
                          background: "#fff",
                        }}
                        preview={true}
                      />
                    </div>
                  )}
                </div>
              </Col>
              <Col xs={24} md={12}>
                <div
                  style={{
                    background: "#f6fbff",
                    borderRadius: 12,
                    padding: 24,
                    height: "100%",
                  }}
                >
                  <div style={{ fontWeight: "bold", marginBottom: 12 }}>
                    Chi tiết đơn thuốc:
                  </div>
                  {group.medicalRequestDetailDTO.map((item, idx2) => (
                    <div
                      key={item.detailId || idx2}
                      style={{
                        background: "#e3f2fd",
                        borderRadius: 8,
                        padding: 12,
                        marginBottom: 10,
                      }}
                    >
                      <div>
                        <b>Tên thuốc:</b> {item.medicineName}
                      </div>
                      <div>
                        <b>Liều lượng:</b> {item.quantity} {item.type}
                      </div>
                      <div>
                        <b>Cách dùng:</b> {item.method}
                      </div>
                      <div>
                        <b>Thời gian:</b> {item.timeSchedule}
                      </div>
                      {/* Tick chọn trạng thái uống thuốc khi ở chế độ ghi nhận */}
                      {isGiveMedicineMode && (
                        <div style={{ margin: "8px 0" }}>
                          <label style={{ marginRight: 16 }}>
                            <input
                              type="radio"
                              checked={medicineStatus[item.detailId] === true}
                              onChange={() =>
                                handleStatusChange(item.detailId, true)
                              }
                            />{" "}
                            Đã uống
                          </label>
                          <label>
                            <input
                              type="radio"
                              checked={medicineStatus[item.detailId] === false}
                              onChange={() =>
                                handleStatusChange(item.detailId, false)
                              }
                            />{" "}
                            Không uống
                          </label>
                        </div>
                      )}
                      <div>
                        <b>Trạng thái uống thuốc:</b>{" "}
                        {isGiveMedicineMode ? (
                          medicineStatus[item.detailId] ? (
                            <span style={{ color: "#21ba45" }}>
                              Đã cho uống
                            </span>
                          ) : (
                            <span style={{ color: "#faad14" }}>
                              Chưa cho uống
                            </span>
                          )
                        ) : item.status === "TAKEN" ? (
                          <span style={{ color: "#21ba45" }}>Đã cho uống</span>
                        ) : (
                          <span style={{ color: "#faad14" }}>
                            Chưa cho uống
                          </span>
                        )}
                      </div>
                      {/* Thêm ô nhập ghi chú khi ở chế độ cho uống thuốc */}
                      {isGiveMedicineMode && (
                        <div style={{ marginTop: 8 }}>
                          <Input.TextArea
                            rows={2}
                            placeholder="Nhập ghi chú cho thuốc này"
                            value={medicineNotes[item.detailId] || ""}
                            onChange={(e) =>
                              handleNoteChange(item.detailId, e.target.value)
                            }
                          />
                        </div>
                      )}
                      {!isGiveMedicineMode && item.note && (
                        <div>
                          <b
                            style={{
                              color: "#d32f2f",
                              fontWeight: "bold",
                              fontSize: "16px",
                            }}
                          >
                            Ghi chú của y tá:
                          </b>{" "}
                          <span
                            style={{
                              color: "#333",
                              backgroundColor: "#fff3cd",
                              padding: "4px 8px",
                              borderRadius: "4px",
                            }}
                          >
                            {item.note}
                          </span>
                        </div>
                      )}
                    </div>
                  ))}
                  {/* Hiển thị lý do từ chối nếu trạng thái là CANCELLED */}
                </div>
              </Col>
            </Row>
          ))}
        </>
      ) : (
        <div>Không có dữ liệu</div>
      )}
      {/* Phần xác nhận cho uống thuốc */}
      {isGiveMedicineMode && (
        <Button
          type="primary"
          loading={confirmLoading}
          block
          style={{ marginTop: 16 }}
          onClick={handleGiveMedicine}
        >
          Lưu ghi chú & xác nhận đã cho uống
        </Button>
      )}
      {/* Phần từ chối giữ nguyên */}
      {isRejectMode && (
        <div style={{ marginTop: 16 }}>
          <Input.TextArea
            rows={3}
            placeholder="Nhập lý do"
            value={reason}
            onChange={onReasonChange}
            style={{ marginBottom: 12 }}
          />
          <Button
            type="primary"
            danger
            loading={confirmLoading}
            block
            onClick={handleReject}
          >
            Xác nhận
          </Button>
        </div>
      )}
      {detailData?.[0]?.status === "CANCELLED" && (
        <div style={{ marginTop: 16 }}>
          <div style={{ fontWeight: "bold", color: "red", marginBottom: 4 }}>
            Lý do từ chối:
          </div>
          <Input.TextArea
            rows={3}
            value={detailData?.[0]?.reasonRejected || "---"}
            readOnly
            style={{ marginBottom: 12, background: "#f5f5f5", color: "#333" }}
          />
        </div>
      )}
    </Modal>
  );
};

export default SendMedicineDetailModal;
