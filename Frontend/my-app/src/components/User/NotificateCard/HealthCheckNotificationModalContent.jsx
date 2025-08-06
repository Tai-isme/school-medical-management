import React, { useEffect, useState } from 'react';
import { Button, Checkbox, Input, Modal, Alert, Descriptions, Typography, Divider } from 'antd';
import HealthCheckConfirmContentModal from './HealthCheckConfirmContentModal';

const { Text } = Typography;

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
    <div style={{ padding: 12 }}>
      <Alert
        message={
          <span>
            <b>Lưu ý:</b> Quý phụ huynh không đăng ký (<b>{notification.healthCheckProgram?.healthCheckName || notification.healthCheckProgramDTO?.healthCheckName || "--"}</b>) cho con vui lòng bỏ qua khảo sát này.
          </span>
        }
        type="info"
        showIcon
        style={{ marginBottom: 16 }}
      />
      <Descriptions
        bordered
        column={1}
        size="small"
        style={{ marginBottom: 16, background: '#fafcff', borderRadius: 8, padding: 8 }}
        labelStyle={{ width: 180, fontWeight: 500 }}
      >
        <Descriptions.Item label="Chương trình">
          {notification.healthCheckProgram?.healthCheckName || notification.healthCheckProgramDTO?.healthCheckName || "--"}
        </Descriptions.Item>
        
        <Descriptions.Item label="Thời gian">
          {notification.healthCheckProgram?.startDate || notification.healthCheckProgramDTO?.startDate || "--"}
        </Descriptions.Item>
        <Descriptions.Item label="Địa điểm">
          {notification.healthCheckProgram?.location || notification.healthCheckProgramDTO?.location || "--"}
        </Descriptions.Item>
        <Descriptions.Item label="Người phụ trách">
          {notification.healthCheckProgram?.nurseDTO?.fullName || notification.healthCheckProgramDTO?.nurseDTO?.fullName || "--"}
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
        </Descriptions.Item>
        <Descriptions.Item label="Ghi chú của phụ huynh">
          <Text type="secondary">{notification.note || "Không có"}</Text>
        </Descriptions.Item>
        <Descriptions.Item label="Mô tả">
          {notification.healthCheckProgram?.description || notification.healthCheckProgramDTO?.description || "--"}
        </Descriptions.Item>
      </Descriptions>
      <Divider />
      <Checkbox
        checked={checked}
        onChange={e => setChecked(e.target.checked)}
        disabled={disabled}
        style={{
          marginBottom: 16,
          display: 'flex',
          alignItems: 'flex-start',
          maxWidth: 500,
          fontSize: 15,
          lineHeight: 1.7,
        }}
      >
        <div>
          Tôi đã đọc kỹ{' '}
          <a
            href="#"
            style={{ color: '#1890ff', textDecoration: 'underline' }}
            onClick={e => {
              e.preventDefault();
              setConfirmModalOpen(true);
            }}
          >
            Nội dung xác nhận miễn trừ trách nhiệm
          </a>
          <br />
          và đồng ý miễn trừ trách nhiệm pháp lý và các giả định rủi ro cho Nhà trường <Text type="danger">*</Text>
        </div>
      </Checkbox>
      <Modal
        open={confirmModalOpen}
        onCancel={() => setConfirmModalOpen(false)}
        footer={null}
        width={500}
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
            notification?.commit === true ||
            notification?.commit === false
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