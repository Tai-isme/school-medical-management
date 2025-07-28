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
      {/* <p>Ghi chú: {notification.notes}</p> */}
      <div
        style={{
          background: "#f6fcf7",
          border: "1px solid #e6f4ea",
          borderRadius: 8,
          padding: 16,
          marginBottom: 16,
        }}
      >
        <b style={{ color: "#21ba45" }}>Nội dung xác nhận miễn trừ trách nhiệm</b>
        <ol style={{ marginTop: 8, paddingLeft: 18 }}>
          <li>
            Khi tôi đồng ý cho con tôi tham gia khám sức khỏe định kỳ được tổ chức vào ngày {notification.healthCheckProgram?.startDate} tại trường, tôi đồng ý với đơn xác nhận này, miễn trừ trách nhiệm pháp lý và các giả định rủi ro.
          </li>
          <li>
            Tôi đã đọc và tìm hiểu các thông tin liên quan tới hoạt động khám sức khỏe định kỳ cho học sinh trước khi đồng ý xác nhận cho con tham gia hoạt động này tại trường.
          </li>
          <li>
            Tôi cam kết đã cho con ăn sáng đầy đủ (đối với học sinh khám vào buổi sáng) và khai báo đầy đủ thông tin được yêu cầu trong thư mời về tình trạng sức khỏe của con.
          </li>
          <li>
            Tôi ý thức được việc đồng ý cho con tham gia khám sức khỏe tại trường có thể gặp phải một số rủi ro như: khó chịu, choáng váng, căng thẳng tâm lý khi thực hiện một số kiểm tra y tế, hoặc những phát hiện bất thường cần can thiệp y tế tiếp theo. Tôi xin chấp nhận và tự chịu trách nhiệm nếu con tôi gặp phải các vấn đề phát sinh trong quá trình khám.
          </li>
          <li>
            Tôi xin đồng ý rằng nếu con tôi cần hỗ trợ y tế hoặc can thiệp khẩn cấp trong quá trình khám, phía Ban Tổ chức và nhà trường có thể sắp xếp việc điều trị và sơ tán khẩn cấp nếu cần thiết và tôi xin chịu trách nhiệm chi trả cho mọi chi phí y tế và chi phí xe cứu thương.
          </li>
          <li>
            Tôi chấp nhận trong trường hợp con tôi gặp sự cố y tế trong quá trình khám sức khỏe tại trường, tôi sẽ không làm đơn khiếu nại nhà trường.
          </li>
        </ol>
      </div>
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