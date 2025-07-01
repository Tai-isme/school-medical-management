import React, { useEffect, useState } from "react";
import axios from "axios";
// import {
//   LineChart, Line, XAxis, YAxis, CartesianGrid,
//   Tooltip, ResponsiveContainer, Legend
// } from "recharts";
import {
  BarChart, Bar, XAxis, YAxis, CartesianGrid,
  Tooltip, ResponsiveContainer, Legend
} from "recharts";

import { useForceResize } from "../../../../hooks/useForceResize";

function MedicalEventsLineChart() {
  useForceResize(); // Hook to force resize on window resize
  const [data, setData] = useState([]);
  const [eventTypes, setEventTypes] = useState([]);
  const colors = [
    "#8884d8", "#82ca9d", "#ffc658", "#ff7f50", "#a4de6c", "#d0ed57", "#8dd1e1"
  ];

  useEffect(() => {
    const token = localStorage.getItem("token");

    axios.get("http://localhost:8080/api/admin/event-stats/monthly", {
      headers: {
        Authorization: `Bearer ${token}`
      },
        params: {
        year: new Date().getFullYear()
      }
    })
    .then(res => {
      const raw = res.data;
      const typesSet = new Set();
      raw.forEach(item => {
        Object.keys(item).forEach(key => {
          if (key !== "month") typesSet.add(key);
        });
      });
      setData(raw);
      setEventTypes(Array.from(typesSet));
    })
    .catch(err => {
      console.error("Lỗi khi gọi API:", err);
    });
  }, []);


    return (
    <div className="p-4">
      <div style={{ width: "100%", height: 300, position: "relative" }}>
      <ResponsiveContainer width="100%" height={300}>
        <BarChart data={data}>
          <CartesianGrid strokeDasharray="3 3" />
          <XAxis dataKey="month" />
          <YAxis />
          <Tooltip />
          <Legend />
          {eventTypes.map((type, index) => (
            <Bar key={type} dataKey={type} fill={colors[index % colors.length]} />
          ))}
        </BarChart>
      </ResponsiveContainer>
      </div>
    </div>
  );
}

export default MedicalEventsLineChart;
