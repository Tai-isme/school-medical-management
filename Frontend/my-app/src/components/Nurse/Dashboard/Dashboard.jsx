import VaccineStatusStatsBarChart from "./charts/VaccineStatusStatsBarChart";
import HealthCheckResultStatsBarChart from "./charts/HealthCheckResultStatsBarChart";
import MedicalEventsStats from "./charts/MedicalEventsStats";
import OverviewCards from "./OverviewCards";
import ChartCard from "./ChartCard";
import React, { useState, useEffect } from "react";
import ProgressCard from "./ProgressCard";
import axios from "axios";

function Dashboard() {

  const [currentTime, setCurrentTime] = useState(new Date());
  const [participationRate, setParticipationRate] = useState({
    vaccination: { programName: "", percent: 0 },
    healthCheck: { programName: "", percent: 0 }
  });

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentTime(new Date());
    }, 1000); 

    const token = localStorage.getItem("token");
    axios.get("http://localhost:8080/api/admin/committed-participation-rate", {
      headers: { Authorization: `Bearer ${token}` }
    }).then(res => {
      setParticipationRate(res.data);
    }).catch(err => {
      console.error("Lỗi khi lấy tỷ lệ tham gia:", err);
    });
    return () => clearInterval(interval);
  }, []);


  return (
    <div
      style={{
        marginLeft: 240,
        minHeight: "100vh",
        padding: "30px",
        background: "linear-gradient(135deg, #e0f7fa, #ffffff)",
      }}
    >
      <div
        style={{
          background: "#ffffff",
          borderRadius: "8px",
          boxShadow: "0 4px 8px rgba(0, 0, 0, 0.1)",
          padding: "20px",
          marginBottom: "20px",
          textAlign: "center",
        }}
      >
        <h2
          style={{
            color: "#00796b",
            fontSize: "28px",
            fontWeight: "bold",
          }}
        >
          <div fontWeight="bold" style={{ display: "inline-block", marginRight: "10px" }}>
            Tổng quan
          </div>
        </h2>
          <p style={{ marginTop: "8px", fontSize: "14px", color: "#555" }}>
            {currentTime.toLocaleString("vi-VN", {
              weekday: "long",
              year: "numeric",
              month: "long",
              day: "numeric",
              hour: "2-digit",
              minute: "2-digit",
              second: "2-digit"
            }).replace("lúc", "")}
          </p>
      </div>

      {/* Tổng quan thống kê */}
      <div>
        <OverviewCards />
      </div>


      <div style={{ marginBottom: "20px" }}>
          <ProgressCard
            title="Tỷ lệ tham gia gần đây nhất"
            items={[
              {
                labelPrefix: " 💉Chương trình tiêm chủng gần đây: ",
                labelSuffix: participationRate.vaccination?.programName || "null",
                percent: participationRate.vaccination.percent,
                color: "#4caf50" // xanh lá
              },
              {
                labelPrefix: " 🩺 Chương trình khám sức khỏe gần đây: ",
                labelSuffix: participationRate.healthCheck?.programName || "null",
                percent: participationRate.healthCheck.percent, // giả sử backend trả về sau
                color: "#2196f3" // xanh dương
              }
            ]}
          />
      </div>

      {/* Biểu đồ */}
      <div
        style={{
          display: "grid",
          gridTemplateColumns: "repeat(auto-fit, minmax(450px, 1fr))",
          gap: "30px",
          marginTop: "20px",
        }}
      >
        <ChartCard title="Thống kê tình trạng sức khỏe theo chương trình tiêm chủng">
          <VaccineStatusStatsBarChart />
        </ChartCard>

        <ChartCard title="Thống kê tình trạng sức khỏe theo chương trình khám định kỳ">
          <HealthCheckResultStatsBarChart />
        </ChartCard>

        <ChartCard title="Số sự cố y tế theo tháng">
          <MedicalEventsStats />
        </ChartCard>
      </div>
    </div>
  );
}

export default Dashboard;