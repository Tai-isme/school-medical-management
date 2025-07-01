import React, { useEffect, useState } from "react";
import axios from "axios";
import {
  BarChart, Bar, XAxis, YAxis, Tooltip,
  CartesianGrid, ResponsiveContainer, Legend
} from "recharts";

import { useForceResize } from "../../../../hooks/useForceResize";

function HealthStatusBarChart() {
  useForceResize(); // Thêm dòng này đầu component
  const [data, setData] = useState([]);
  const [statusKeys, setStatusKeys] = useState([]);

    useEffect(() => {
        const token = localStorage.getItem("token");
        axios.get("http://localhost:8080/api/admin/health-check-results-status-by-program", {
            headers: { Authorization: `Bearer ${token}` }
        })
        .then(res => {
            const raw = res.data;
            const grouped = {};
            const statusMap = {
            GOOD: "Tốt",
            FAIR: "Khá",
            AVERAGE: "Trung bình",
            POOR: "Kém"
            };

            raw.forEach(item => {
            const { programName, statusHealth, count } = item;
            const translatedStatus = statusMap[statusHealth] || statusHealth;
            if (!grouped[programName]) grouped[programName] = { programName };
            grouped[programName][translatedStatus] = count;
            });

            const finalData = Object.values(grouped);
            const statusSet = new Set(raw.map(i => statusMap[i.statusHealth] || i.statusHealth));
            setStatusKeys(Array.from(statusSet));
            setData(finalData);
        })
        .catch(err => {
            console.error("Lỗi khi lấy dữ liệu sức khỏe:", err);
        });
    }, []);


  return (
    <div>
      <div style={{ width: "100%", height: 300, position: "relative" }}>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="programName" />
          <YAxis />
          <Tooltip />
          <Legend />
          {statusKeys.map((key, index) => (
            <Bar key={key} dataKey={key} fill={colors[index % colors.length]} />
          ))}
        </BarChart>
      </ResponsiveContainer>
      </div>
    </div>
  );
}

const colors = ["#4caf50", "#ff9800", "#f44336", "#2196f3", "#9c27b0"];

export default HealthStatusBarChart;
