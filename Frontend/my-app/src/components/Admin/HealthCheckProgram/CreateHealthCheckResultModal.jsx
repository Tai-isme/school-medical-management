import React from "react";
import { Modal, Table, Input } from "antd";

const columns = [
  { title: "ID học sinh", dataIndex: "studentId", key: "studentId" },
  { title: "Tên học sinh", dataIndex: "studentName", key: "studentName" },
  { title: "Chẩn đoán", dataIndex: "diagnosis", key: "diagnosis", render: (_, record, idx) => <Input value={record.diagnosis} onChange={e => record.onChange(e, idx, "diagnosis")} /> },
  { title: "Mức độ", dataIndex: "level", key: "level", render: (_, record, idx) => <Input value={record.level} onChange={e => record.onChange(e, idx, "level")} /> },
  { title: "Ghi chú", dataIndex: "note", key: "note", render: (_, record, idx) => <Input value={record.note} onChange={e => record.onChange(e, idx, "note")} /> },
  { title: "Thị lực", dataIndex: "vision", key: "vision", render: (_, record, idx) => <Input value={record.vision} onChange={e => record.onChange(e, idx, "vision")} /> },
  { title: "Thính lực", dataIndex: "hearing", key: "hearing", render: (_, record, idx) => <Input value={record.hearing} onChange={e => record.onChange(e, idx, "hearing")} /> },
  { title: "Cân nặng", dataIndex: "weight", key: "weight", render: (_, record, idx) => <Input value={record.weight} onChange={e => record.onChange(e, idx, "weight")} /> },
  { title: "Chiều cao", dataIndex: "height", key: "height", render: (_, record, idx) => <Input value={record.height} onChange={e => record.onChange(e, idx, "height")} /> },
];

const CreateHealthCheckResultModal = ({ open, onCancel, students, onChange, onOk, loading }) => {
  // students: [{studentId, studentName, ...fields}]
  // onChange: (value, idx, field) => void

  // Thêm hàm onChange vào từng record
  const dataSource = students.map((item, idx) => ({
    ...item,
    onChange: (e, i, field) => onChange(e.target.value, idx, field),
  }));

  return (
    <Modal
      open={open}
      title="Tạo kết quả khám định kỳ"
      onCancel={onCancel}
      onOk={onOk}
      width={1200}
      confirmLoading={loading}
      okText="Lưu kết quả"
    >
      <Table
        columns={columns}
        dataSource={dataSource}
        rowKey="studentId"
        pagination={false}
        scroll={{ x: true }}
      />
    </Modal>
  );
};

export default CreateHealthCheckResultModal;