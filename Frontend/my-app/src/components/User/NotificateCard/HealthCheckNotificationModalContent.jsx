import React, { useEffect, useState } from 'react';
import { Button, Checkbox, Input, Modal } from 'antd';
import HealthCheckConfirmContentModal from './HealthCheckConfirmContentModal';

const HealthCheckNotificationModalContent = ({
  notification,
  checked,
  setChecked,
  note,
  setNote,
  onSubmit,
  loading,
  disabled,
}) => {
  const [confirmModalOpen, setConfirmModalOpen] = useState(false);

  useEffect(() => {
    if (notification?.commit === true) {
      setChecked(true);
      setNote(notification?.notes || "");
    } else if (notification?.commit === false) {
      setChecked(false);
      setNote(notification?.notes || "");
    } else {
      setChecked(false);
      setNote("");
    }
    // eslint-disable-next-line
  }, [notification]);

  return (
    <div>
      <div style={{ marginBottom: 16 }}>
        <p>
          <b>Chương trình:</b> {notification.healthCheckProgram?.healthCheckName || notification.healthCheckProgramDTO?.healthCheckName || "--"}
        </p>
        <p>
          <b>Mô tả:</b> {notification.healthCheckProgram?.description || notification.healthCheckProgramDTO?.description || "--"}
        </p>
        <p>
          <b>Thời gian:</b> {notification.healthCheckProgram?.startDate || notification.healthCheckProgramDTO?.startDate || "--"}
        </p>
        <p>
          <b>Địa điểm:</b> {notification.healthCheckProgram?.location || notification.healthCheckProgramDTO?.location || "--"}
        </p>
        <p>
          <b>Người phụ trách:</b> {notification.healthCheckProgram?.nurseDTO?.fullName || notification.healthCheckProgramDTO?.nurseDTO?.fullName || "--"}
          {(() => {
            const nurse =
              notification.healthCheckProgram?.nurseDTO ||
              notification.healthCheckProgramDTO?.nurseDTO;
            if (nurse) {
              return (
                <>
                  <br />
                  <span style={{ marginLeft: 16 }}>
                    <b>SĐT:</b> {nurse.phone || "--"}
                  </span>
                  <br />
                  <span style={{ marginLeft: 16 }}>
                    <b>Email:</b> {nurse.email || "--"}
                  </span>
                </>
              );
            }
            return null;
          })()}
        </p>
      </div>
      <Checkbox
        checked={checked}
        onChange={e => setChecked(e.target.checked)}
        disabled={disabled}
        style={{ marginBottom: 8 }}
      >
        Tôi đã đọc kỹ <a href="#" onClick={e => { e.preventDefault(); setConfirmModalOpen(true); }}>Nội dung xác nhận miễn trừ trách nhiệm</a> và đồng ý miễn trừ trách nhiệm pháp lý và các giả định rủi ro cho Nhà trường
      </Checkbox>
      <Modal
        open={confirmModalOpen}
        onCancel={() => setConfirmModalOpen(false)}
        footer={null}
        width={500} // Đổi từ 700 thành 500 để modal ngắn hơn
        centered
        title="Nội dung xác nhận miễn trừ trách nhiệm khảo sát sức khỏe"
      >
        <HealthCheckConfirmContentModal />
      </Modal>
      {checked && !disabled && (
        <Input.TextArea
          placeholder="Ghi chú (nếu có, ví dụ: không được làm gì với học sinh của tôi)"
          value={note}
          onChange={e => setNote(e.target.value)}
          rows={3}
          style={{ marginBottom: 16 }}
        />
      )}
      <div style={{ textAlign: 'right', marginTop: 8 }}>
        <Button
          type="primary"
          onClick={() => onSubmit && onSubmit({ checked, note, notification })}
          disabled={
            disabled ||
            !checked ||
            (notification?.commit !== null && notification?.commit !== undefined)
          }
          loading={loading}
        >
          Gửi
        </Button>
      </div>
    </div>
  );
};

export default HealthCheckNotificationModalContent;