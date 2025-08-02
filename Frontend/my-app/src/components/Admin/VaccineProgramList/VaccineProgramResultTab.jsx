import React, { useState } from "react";
import { Input, Table, Modal, Form, Button, DatePicker, Radio } from "antd";
import dayjs from "dayjs";
import { SearchOutlined } from "@ant-design/icons";

const VaccineProgramResultTab = ({
  searchTermResult,
  setSearchTermResult,
  sampleResultData,
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
}) => {
  // Modal state
  const [modalVisible, setModalVisible] = useState(false);
  const [currentRecord, setCurrentRecord] = useState(null);
  const [modalForm] = Form.useForm();

  // Khi bấm "Ghi nhận"
  const handleOpenModal = (record) => {
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

      await fetch("http://localhost:8080/api/nurse/vaccine-result", {
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
          isInjected: values.isInjected, // Đúng theo API mới
          vaccineFormId: vaccineFormId,
        }),
      });
      setModalVisible(false);
      // Có thể reload lại bảng ở đây nếu cần
    } catch (err) {
      // Xử lý lỗi nếu cần
    }
  };

  // Khi hủy modal
  const handleModalCancel = () => {
    setModalVisible(false);
  };

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
          columns={[
            {
              title: "ID kết quả",
              dataIndex: "id",
              key: "id",
            },
            {
              title: "Học sinh",
              dataIndex: ["studentDTO", "fullName"],
              key: "studentName",
              render: (_, record) => record.studentDTO?.fullName,
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
              render: (_, record) => record.studentDTO?.gender === "MALE" ? "Nam" : "Nữ",
            },
            {
              title: "Phản ứng sau tiêm",
              dataIndex: "reaction",
              key: "reaction",
              render: (text, record) =>
                sampleResultData ? (
                  <Input
                    value={
                      editableRows.find((r) => r.id === record.id)?.reaction ||
                      record.vaccineResultDTO?.reaction ||
                      text ||
                      ""
                    }
                    onChange={(e) =>
                      handleEditCell(e.target.value, record, "reaction")
                    }
                    style={{ minWidth: 100 }}
                  />
                ) : (
                  record.vaccineResultDTO?.reaction || text || ""
                ),
            },
            {
              title: "Cách xử lý",
              dataIndex: "reaction",
              key: "reaction",
              render: (text, record) =>
                sampleResultData ? (
                  <Input
                    value={
                      editableRows.find((r) => r.id === record.id)?.reaction ||
                      record.vaccineResultDTO?.reaction ||
                      ""
                    }
                    onChange={(e) =>
                      handleEditCell(e.target.value, record, "reaction")
                    }
                    style={{ minWidth: 100 }}
                  />
                ) : (
                  record.vaccineResultDTO?.reaction || text || ""
                ),
            },
            {
              title: "Mô tả chi tiết",
              dataIndex: "resultNote",
              key: "resultNote",
              render: (text, record) =>
                sampleResultData ? (
                  <Input
                    value={
                      editableRows.find((r) => r.id === record.id)?.resultNote ||
                      record.vaccineResultDTO?.resultNote ||
                      ""
                    }
                    onChange={(e) =>
                      handleEditCell(e.target.value, record, "resultNote")
                    }
                    style={{ minWidth: 120 }}
                  />
                ) : (
                  record.vaccineResultDTO?.resultNote || text || ""
                ),
            },
            {
              title: "Ngày tạo",
              dataIndex: "createdAt",
              key: "createdAt",
              render: (text) => text ? dayjs(text).format("YYYY-MM-DD") : "",
            },
            {
              title: "Đã tiêm?",
              key: "commit",
              render: (_, record) => {
                // Ưu tiên lấy từ vaccineResultDTO.isInjected
                if (
                  record.vaccineResultDTO &&
                  typeof record.vaccineResultDTO.isInjected === "boolean"
                ) {
                  return record.vaccineResultDTO.isInjected ? "Đã tiêm" : "Chưa tiêm";
                }
                // Sau đó lấy từ isInjected trực tiếp
                if (typeof record.isInjected === "boolean") {
                  return record.isInjected ? "Đã tiêm" : "Chưa tiêm";
                }
                // Cuối cùng lấy từ commit
                return record.commit ? "Đã tiêm" : "Chưa tiêm";
              },
            },
            sampleResultData && {
              title: "Thao tác",
              key: "action",
              render: (_, record) => (
                <Button
                  type="primary"
                  onClick={() => handleOpenModal(record)}
                >
                  Ghi nhận
                </Button>
              ),
            },
          ].filter(Boolean)}
          dataSource={
            sampleResultData
              ? editableRows.filter((item) =>
                  (item?.studentDTO?.fullName || "")
                    .toLowerCase()
                    .includes(searchTermResult.toLowerCase())
                )
              : selectedVaccineResultId
              ? (selectedVaccineResult || []).filter((item) =>
                  (item?.studentDTO?.fullName || "")
                    .toLowerCase()
                    .includes(searchTermResult.toLowerCase())
                )
              : filteredNurseResults
          }
          loading={selectedVaccineResultLoading || nurseResultsLoading}
          rowKey="id"
          bordered
          style={{
            paddingLeft: 2,
            width: "100%",
            minWidth: 1600,
            borderRadius: 12,
            overflow: "auto",
            background: "#fff",
            boxShadow: "0 2px 8px rgba(33,186,69,0.08)",
          }}
          scroll={{ x: true }}
          pagination={{
            current: resultTablePage,
            pageSize: resultTablePageSize,
            total: sampleResultData
              ? editableRows.filter((item) =>
                  (item?.studentDTO?.fullName || "")
                    .toLowerCase()
                    .includes(searchTermResult.toLowerCase())
                ).length
              : selectedVaccineResultId
              ? (selectedVaccineResult || []).filter((item) =>
                  (item?.studentDTO?.fullName || "")
                    .toLowerCase()
                    .includes(searchTermResult.toLowerCase())
                ).length
              : filteredNurseResults.length,
            onChange: setResultTablePage,
            showSizeChanger: false,
          }}
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
            <Form.Item
              label="Mô tả chi tiết"
              name="resultNote"
            >
              <Input.TextArea rows={2} />
            </Form.Item>
            <Form.Item
              label="Ghi chú:"
              name="detailNote"
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