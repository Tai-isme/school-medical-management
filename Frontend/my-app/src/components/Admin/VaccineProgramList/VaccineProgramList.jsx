import React, { useState, useEffect } from "react";
import { Input, Button, Modal, Checkbox, message } from "antd";
import axios from "axios";
import "./VaccineProgramList.css";

const VaccineProgramList = () => {
  const [searchText, setSearchText] = useState("");
  const [sortAsc, setSortAsc] = useState(true);
  const [sortByStatusAsc, setSortByStatusAsc] = useState(true);
  const [data, setData] = useState([]);
  const [isDetailModalOpen, setIsDetailModalOpen] = useState(false);
  const [selectedProgram, setSelectedProgram] = useState(null);
  const [selectedRowKeys, setSelectedRowKeys] = useState([]);

  useEffect(() => {
    fetchData();
  }, []);

  const fetchData = async () => {
    try {
      const token = localStorage.getItem("token");
      const res = await axios.get(
        "http://localhost:8080/api/admin/vaccine-program",
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setData(res.data);
    } catch (err) {
      message.error("Không thể tải dữ liệu chương trình!");
    }
  };

  const filteredData = data
    .filter((program) =>
      program.name?.toLowerCase().includes(searchText.toLowerCase())
    )
    .sort((a, b) => {
      const dateA = new Date(a.startDate);
      const dateB = new Date(b.startDate);
      return sortAsc ? dateA - dateB : dateB - dateA;
    })
    .sort((a, b) => {
      if (!sortByStatusAsc) {
        return b.status.localeCompare(a.status);
      }
      return a.status.localeCompare(b.status);
    });

  const allIds = filteredData.map((p) => p.id);
  const isAllSelected =
    allIds.length > 0 && allIds.every((id) => selectedRowKeys.includes(id));

  const getStatusClass = (status) => {
    switch (status) {
      case "ON_GOING":
        return "on_going";
      case "COMPLETED":
        return "completed";
      case "NOT_STARTED":
        return "not_started";
      default:
        return "";
    }
  };

  const handleSelect = (id) => {
    setSelectedRowKeys((prev) =>
      prev.includes(id) ? prev.filter((i) => i !== id) : [...prev, id]
    );
  };

  const handleSend = async () => {
    try {
      const token = localStorage.getItem("token");

      const payload = {
        formIds: selectedRowKeys,
      };

      await axios.post("http://localhost:8080/api/notify-vaccine", payload, {
        headers: {
          Authorization: `Bearer ${token}`,
          "Content-Type": "application/json",
        },
      });

      message.success(
        `Đã gửi ${selectedRowKeys.length} chương trình thành công.`
      );
      setSelectedRowKeys([]);
      fetchData();
    } catch (error) {
      console.error("Lỗi khi gửi chương trình:", error);
      message.error("Gửi thất bại. Vui lòng thử lại.");
    }
  };

  return (
    <div className="hcp-container">
      <div className="hcp-header-bar">
        <div className="hcp-title">Chương trình khám sức khỏe</div>
        <Input
          placeholder="Tìm kiếm theo tên..."
          value={searchText}
          onChange={(e) => setSearchText(e.target.value)}
          style={{ width: 250 }}
          allowClear
        />
      </div>

      <div className="hcp-table-wrapper">
        <table className="hcp-table">
          <thead>
            <tr>
              <th>
                <Checkbox
                  checked={isAllSelected}
                  indeterminate={selectedRowKeys.length > 0 && !isAllSelected}
                  onChange={(e) => {
                    setSelectedRowKeys(e.target.checked ? allIds : []);
                  }}
                />
              </th>
              <th>Tên chương trình</th>
              <th>
                <Button
                  type="link"
                  onClick={() => setSortAsc(!sortAsc)}
                  style={{ padding: 0 }}
                >
                  Thời gian {sortAsc ? "↑" : "↓"}
                </Button>
              </th>
              <th>
                <Button
                  type="link"
                  onClick={() => setSortByStatusAsc(!sortByStatusAsc)}
                  style={{ padding: 0 }}
                >
                  Trạng thái {sortByStatusAsc ? "↑" : "↓"}
                </Button>
              </th>
              <th>Ghi chú</th>
            </tr>
          </thead>
          <tbody>
            {filteredData.length === 0 ? (
              <tr>
                <td
                  colSpan="6"
                  style={{ textAlign: "center", padding: 20, color: "#888" }}
                >
                  Không có chương trình nào phù hợp.
                </td>
              </tr>
            ) : (
              filteredData.map((program) => (
                <tr key={program.id}>
                  <td>
                    <Checkbox
                      checked={selectedRowKeys.includes(program.id)}
                      onChange={() => handleSelect(program.id)}
                    />
                  </td>
                  <td
                    onClick={() => {
                      setSelectedProgram(program);
                      setIsDetailModalOpen(true);
                    }}
                    style={{ cursor: "pointer" }}
                  >
                    <div>
                      <strong>{program.name}</strong>
                    </div>
                    <div className="hcp-description">{program.description}</div>
                  </td>
                  <td>
                    {program.startDate} → {program.endDate}
                  </td>
                  <td>
                    <span
                      className={`hcp-status ${getStatusClass(program.status)}`}
                    >
                      {program.status.replace("_", " ").toLowerCase()}
                    </span>
                  </td>
                  <td>{program.note || "-"}</td>
                </tr>
              ))
            )}
          </tbody>
        </table>
        {selectedRowKeys.length > 0 && (
          <div style={{ textAlign: "right", marginTop: 16 }}>
            <Button type="primary" onClick={handleSend}>
              Gửi
            </Button>
          </div>
        )}
      </div>

      <Modal
        title="Chi tiết chương trình"
        open={isDetailModalOpen}
        onCancel={() => setIsDetailModalOpen(false)}
        footer={null}
      >
        {selectedProgram && (
          <div>
            <p>
              <strong>Tên:</strong> {selectedProgram.name}
            </p>
            <p>
              <strong>Mô tả:</strong> {selectedProgram.description}
            </p>
            <p>
              <strong>Thời gian:</strong> {selectedProgram.startDate} →{" "}
              {selectedProgram.endDate}
            </p>
            <p>
              <strong>Trạng thái:</strong> {selectedProgram.status}
            </p>
            <p>
              <strong>Ghi chú:</strong> {selectedProgram.note || "-"}
            </p>
          </div>
        )}
      </Modal>
    </div>
  );
};

export default VaccineProgramList;
