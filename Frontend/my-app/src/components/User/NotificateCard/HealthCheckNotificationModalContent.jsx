import React, { useEffect } from 'react';
import { Button, Radio, Input } from 'antd';

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
  useEffect(() => {
    if (notification?.commit === true) {
      setChecked("agree");
      setNote(notification?.notes || "");
    } else if (notification?.commit === false) {
      setChecked("disagree");
      setNote(notification?.notes || "");
    } else {
      setChecked(null);
      setNote("");
    }
    // eslint-disable-next-line
  }, [notification]);

  return (
    <div>
      <p>Thông tin khảo sát sức khỏe: <b>{notification.healthCheckProgram?.name}</b></p>
      <p>Mô tả: {notification.healthCheckProgram?.description}</p>
      <p>Thời gian: {notification.healthCheckProgram?.startDate} - {notification.healthCheckProgram?.endDate}</p>
      <p>Ghi chú: {notification.healthCheckProgram.note}</p>
      <Radio.Group
        onChange={e => setChecked(e.target.value)}
        value={checked}
        style={{ marginBottom: 8 }}
        disabled={notification?.commit !== null && notification?.commit !== undefined}
      >
        <Radio value="agree" disabled={notification?.commit !== null && notification?.commit !== undefined}>
          Tôi đồng ý với đơn xác nhận này, miễn trừ trách nhiệm pháp lý và các giả định rủi ro cho Nhà trường
        </Radio>
        <Radio value="disagree" disabled={notification?.commit !== null && notification?.commit !== undefined}>
          Tôi không đồng ý với đơn xác nhận miễn trừ trách nhiệm này
        </Radio>
      </Radio.Group>
      {checked && (
        <Input.TextArea
          placeholder={
            checked === 'disagree'
              ? 'Lý do không đồng ý (nếu có)'
              : 'Ghi chú (nếu có, ví dụ: không được làm gì với học sinh của tôi)'
          }
          value={note}
          onChange={e => setNote(e.target.value)}
          rows={3}
          style={{ marginBottom: 16 }}
          disabled={notification?.commit !== null && notification?.commit !== undefined}
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