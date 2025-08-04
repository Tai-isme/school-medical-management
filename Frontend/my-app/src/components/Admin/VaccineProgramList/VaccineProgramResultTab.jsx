import React, { useState } from "react";
import { Input, Table, Modal, Form, Button, DatePicker, Radio } from "antd";
import dayjs from "dayjs";
import { SearchOutlined } from "@ant-design/icons";
import { urlServer } from "../../../api/urlServer";

const VaccineProgramResultTab = ({
  program, // Nhận prop này
  searchTermResult,
  setSearchTermResult,
  sampleResultData,
  setSampleResultData, // Thêm prop này
  editableRows,
  handleEditCell,
  handleSaveRow,
  selectedVaccineResultId,
  selectedVaccineResult,
  filteredNurseResults,
  selectedVaccineResultLoading,
  nurseResultsLoading,
  resultTablePage,
  resultTablePageSize,
  setResultTablePage,
  handleEditResult, // <-- thêm prop này
  viewMode, // Thêm prop này
}) => {
  // Modal state
  const [modalVisible, setModalVisible] = useState(false);
  const [currentRecord, setCurrentRecord] = useState(null);
  const [modalForm] = Form.useForm();
console.log("VaccineProgramResultTab rendered with program:", program);
console.log("viewMode:", viewMode);
  // Khi bấm "Ghi nhận"
  const handleOpenModal = (record) => {
     console.log("Record khi mở modal:", record);
    setCurrentRecord(record);
    modalForm.setFieldsValue({
      reaction: record.reaction || "",
      actionsTaken: record.actionsTaken || "",
      resultNote: record.resultNote || "",
      statusHealth: record.statusHealth || "", // Nếu có trường này
      detailNote: record.detailNote || "",     // Nếu có trường này
      actualDate: record.actualDate ? dayjs(record.actualDate) : null,
      isInjected: typeof record.isInjected === "boolean" ? record.isInjected : true,
    });
    setModalVisible(true);
  };


  // Khi xác nhận trong modal
const handleModalOk = async () => {
  try {
    const values = await modalForm.validateFields();
    const token = localStorage.getItem("token");
    const vaccineFormId = currentRecord?.vaccineFormId || currentRecord?.id;
    const programId = program?.vaccineProgramId;

    // Ghi nhận kết quả
    await fetch(
      `${urlServer}/api/nurse/create-vaccineResults-byProgram/${programId}`,
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
        body: JSON.stringify({
          resultNote: values.resultNote,
          reaction: values.reaction,
          actionsTaken: values.actionsTaken,
          createAt: new Date().toISOString(),
          isInjected: values.isInjected,
          vaccineFormId: vaccineFormId,
        }),
      }
    );

    // Gọi lại API để lấy dữ liệu mới nhất
    const res = await fetch(
      `${urlServer}/api/nurse/vaccine-result/program/${programId}`,
      {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      }
    );
    const data = await res.json();

    // Map lại dữ liệu để Table đọc đúng
    const mappedData = data.map(item => ({
      vaccineResultId: item.vaccineResultDTO?.vaccineResultId || null,
      vaccineFormId: item.id,
      reaction: item.vaccineResultDTO?.reaction || "",
      actionsTaken: item.vaccineResultDTO?.actionsTaken || "",
      resultNote: item.vaccineResultDTO?.resultNote || "",
      isInjected: typeof item.vaccineResultDTO?.isInjected === "boolean" ? item.vaccineResultDTO.isInjected : false,
      createdAt: item.vaccineResultDTO?.createdAt || "",
      studentDTO: item.studentDTO || null,
    }));

    setSampleResultData(mappedData); // dùng props
    setModalVisible(false);
  } catch (err) {
    setModalVisible(false);
  }
};


  // Khi hủy modal
  const handleModalCancel = () => {
    setModalVisible(false);
  };


  const convertResultToTableRow = (result, studentInfo) => ({
    vaccineResultId: result.vaccineResultId,
    vaccineFormId: result.vaccineFormId,
    reaction: result.reaction,
    actionsTaken: result.actionsTaken,
    createdAt: result.createdAt,
    isInjected: result.isInjected,
    // Nếu cần thêm thông tin học sinh, truyền vào studentInfo
    studentDTO: studentInfo || null,
  });


  const filteredData = (sampleResultData || []).filter(item =>
    item.studentDTO?.fullName?.toLowerCase().includes(searchTermResult?.toLowerCase() || "")
  );

  const columns = [
    {
      title: "Mã học sinh",
      dataIndex: ["studentDTO", "studentId"],
      key: "studentId",
      render: (_, record) => record.studentDTO?.studentId,
      sorter: (a, b) => (a.studentDTO?.studentId || 0) - (b.studentDTO?.studentId || 0),
    },
    {
      title: "Học sinh",
      dataIndex: ["studentDTO", "fullName"],
      key: "studentName",
      render: (_, record) => record.studentDTO?.fullName,
      sorter: (a, b) =>
        (a.studentDTO?.fullName || "").localeCompare(b.studentDTO?.fullName || ""),
    },
    {
      title: "Lớp",
      dataIndex: ["studentDTO", "classDTO", "className"],
      key: "className",
      render: (_, record) => record.studentDTO?.classDTO?.className || "",
    },
    {
      title: "Giới tính",
      dataIndex: ["studentDTO", "gender"],
      key: "gender",
      render: (_, record) =>
        record.studentDTO?.gender === "MALE" ? "Nam" : "Nữ",
    },
    {
      title: "Phản ứng sau tiêm",
      dataIndex: "reaction",
      key: "reaction",
    },
    {
      title: "Cách xử lý",
      dataIndex: "actionsTaken",
      key: "actionsTaken",
    },
    {
      title: "Mô tả chi tiết",
      dataIndex: "resultNote",
      key: "resultNote",
    },
    {
      title: "Ngày tạo",
      dataIndex: "createdAt",
      key: "createdAt",
      render: (text) => text ? dayjs(text).format("YYYY-MM-DD") : "",
    },
    {
      title: "Đã tiêm?",
      dataIndex: "isInjected",
      key: "isInjected",
      render: (val) => val ? "Đã tiêm" : "Chưa tiêm",
    },
    {
      title: "Thao tác",
      key: "action",
      render: (_, record) =>
        !viewMode && Array.isArray(editableRows) && editableRows.length > 0 ? (
          <Button type="primary" onClick={() => handleOpenModal(record)}>
            Ghi nhận
          </Button>
        ) : null,
    },
  ];

  return (
    <div style={{ marginTop: 24 }}>
      <div
        style={{
          background: "#fff",
          borderRadius: 10,
          padding: 24,
          width: "100%",
          maxWidth: "100%",
          margin: "0 auto",
          boxShadow: "0 2px 8px rgba(0,0,0,0.05)",
          overflowX: "auto",
        }}
      >
        {/* Hiển thị tên chương trình ở trên */}
        {program?.vaccineProgramName && (
          <div style={{
            fontWeight: 700,
            fontSize: 20,
            color: "#1976d2",
            marginBottom: 16,
            textAlign: "center"
          }}>
            {program.vaccineProgramName}
          </div>
        )}
        <div
          style={{
            marginBottom: 16,
            display: "flex",
            justifyContent: "flex-end",
          }}
        >
          <Input
            placeholder="Tìm kiếm tên học sinh..."
            prefix={<SearchOutlined />}
            value={searchTermResult}
            onChange={(e) => setSearchTermResult(e.target.value)}
            allowClear
            style={{ width: 260 }}
          />
        </div>
        <Table
          columns={viewMode ? columns.filter(col => col.key !== "action") : columns}
          dataSource={filteredData.sort((a, b) =>
            (a.studentDTO?.fullName || "").localeCompare(b.studentDTO?.fullName || "")
          )}
          rowKey={record => record.studentDTO?.studentId || Math.random()}
          pagination={{
            current: resultTablePage,
            pageSize: resultTablePageSize,
            total: filteredData.length,
            onChange: setResultTablePage,
            showSizeChanger: false,
          }}
          bordered
        />


        {/* Modal nhập thông tin */}
        <Modal
          title={<div style={{ textAlign: "center", color: "#1976d2", fontWeight: 700, fontSize: 22 }}>GHI NHẬN KẾT QUẢ TIÊM CHỦNG</div>}
          open={modalVisible}
          onOk={handleModalOk}
          onCancel={handleModalCancel}
          okText="Lưu"
          cancelText="Hủy"
          footer={[
            <Button key="cancel" onClick={handleModalCancel}>
              Hủy
            </Button>,
            <Button key="submit" type="primary" onClick={handleModalOk}>
              Lưu
            </Button>,
          ]}
        >
          <Form
            form={modalForm}
            layout="vertical"
            initialValues={{
              isInjected: true,
            }}
          >
            <Form.Item label="Thông tin học sinh:">
              <Input
                value={
                  currentRecord
                    ? `${currentRecord.studentDTO?.fullName || ""} - Lớp: ${currentRecord.studentDTO?.className || ""}`
                    : ""
                }
                disabled
              />
            </Form.Item>


            <Form.Item
              label="Kết quả tiêm chủng:"
              name="isInjected"
              rules={[{ required: true, message: "Vui lòng chọn kết quả" }]}
            >
              <Radio.Group>
                <Radio value={true}>Thành công</Radio>
                <Radio value={false}>Thất bại</Radio>
              </Radio.Group>
            </Form.Item>


            <Form.Item
              label="Phản ứng sau tiêm"
              name="reaction"
            >
              <Input />
            </Form.Item>
            <Form.Item
              label="Cách xử lý"
              name="actionsTaken"
            >
              <Input />
            </Form.Item>
            {/* <Form.Item
              label="Mô tả chi tiết"
              name="resultNote"
            >
              <Input.TextArea rows={2} />
            </Form.Item> */}
            <Form.Item
              label="Ghi chú:"
              name="resultNote"
            >
              <Input.TextArea rows={3} />
            </Form.Item>
          </Form>
        </Modal>
      </div>
    </div>
  );
};


export default VaccineProgramResultTab;

