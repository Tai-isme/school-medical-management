import React from "react";
import { Input, Table } from "antd";
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
}) => (
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
            dataIndex: "vaccineResultId",
            key: "vaccineResultId",
          },
          {
            title: "Học sinh",
            dataIndex: ["studentDTO", "fullName"],
            key: "studentName",
            render: (_, record) => record.studentDTO?.fullName,
          },
          {
            title: "Giới tính",
            dataIndex: ["studentDTO", "gender"],
            key: "gender",
            render: (_, record) => record.studentDTO?.gender,
          },
          {
            title: "Phản ứng sau tiêm",
            dataIndex: "statusHealth",
            key: "statusHealth",
            render: (text, record) =>
              sampleResultData ? (
                <Input
                  value={
                    editableRows.find(
                      (r) => r.vaccineResultId === record.vaccineResultId
                    )?.statusHealth
                  }
                  onChange={(e) =>
                    handleEditCell(e.target.value, record, "statusHealth")
                  }
                  style={{ minWidth: 100 }}
                />
              ) : (
                text
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
                    editableRows.find(
                      (r) => r.vaccineResultId === record.vaccineResultId
                    )?.reaction
                  }
                  onChange={(e) =>
                    handleEditCell(e.target.value, record, "reaction")
                  }
                  style={{ minWidth: 100 }}
                />
              ) : (
                text
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
                    editableRows.find(
                      (r) => r.vaccineResultId === record.vaccineResultId
                    )?.resultNote
                  }
                  onChange={(e) =>
                    handleEditCell(e.target.value, record, "resultNote")
                  }
                  style={{ minWidth: 120 }}
                />
              ) : (
                text
              ),
          },
          {
            title: "Ngày tạo",
            dataIndex: "createdAt",
            key: "createdAt",
            render: (text) => dayjs(text).format("YYYY-MM-DD"),
          },
          {
            title: "Tên vaccine",
            key: "vaccineName",
            render: (_, record) =>
              record.vaccineFormDTO?.vaccineProgram?.vaccineName?.vaccineName,
          },
          sampleResultData && {
            title: "Thao tác",
            key: "action",
            render: (_, record) => (
              <button
                type="button"
                className="ant-btn ant-btn-primary"
                onClick={() =>
                  handleSaveRow(
                    editableRows.find(
                      (r) => r.vaccineResultId === record.vaccineResultId
                    )
                  )
                }
              >
                Lưu
              </button>
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
        rowKey="vaccineResultId"
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
    </div>
  </div>
);

export default VaccineProgramResultTab;