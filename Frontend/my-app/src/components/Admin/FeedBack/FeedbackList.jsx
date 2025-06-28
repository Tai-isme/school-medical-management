import React, { useEffect, useState } from "react";
import { Table, Tag, Spin, message } from "antd";
import axios from "axios";

const FeedbackList = () => {
  const [feedbacks, setFeedbacks] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    fetchFeedbacks();
  }, []);

  const fetchFeedbacks = async () => {
    try {
      const token = localStorage.getItem("token");
      const parentId = 9007199254740991; // Thay bằng parentId thực tế hoặc truyền từ props
      const res = await axios.get(
        `http://localhost:8080/api/parent/getfeedback/${parentId}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setFeedbacks(res.data);
    } catch (error) {
      message.error("Không thể tải dữ liệu phản hồi");
    } finally {
      setLoading(false);
    }
  };

  const columns = [
    {
      title: "ID",
      dataIndex: "feedbackId",
      key: "feedbackId",
    },
    {
      title: "Mức độ hài lòng",
      dataIndex: "satisfaction",
      key: "satisfaction",
    },
    {
      title: "Bình luận",
      dataIndex: "comment",
      key: "comment",
    },
    {
      title: "Trạng thái",
      dataIndex: "status",
      key: "status",
      render: (status) => (
        <Tag color={status === "Đã xử lý" ? "green" : "red"}>{status}</Tag>
      ),
    },
    {
      title: "Parent ID",
      dataIndex: "parentId",
      key: "parentId",
    },
    {
      title: "Vaccine Result ID",
      dataIndex: "vaccineResultId",
      key: "vaccineResultId",
    },
    {
      title: "Health Result ID",
      dataIndex: "healthResultId",
      key: "healthResultId",
    },
  ];

  return (
    <div
      style={{
        padding: "20px",
        marginLeft: "240px",
        minHeight: "100vh",
        background: "#f6fbff",
      }}
    >
      <h2>📋 Danh sách phản hồi</h2>
      {loading ? (
        <Spin tip="Đang tải..." />
      ) : (
        <Table
          dataSource={feedbacks}
          columns={columns}
          rowKey="feedbackId"
          pagination={{ pageSize: 5 }}
        />
      )}
    </div>
  );
};

export default FeedbackList;
