import React from "react";

function ProgressCard({ title, items }) {
  return (
    <div
      style={{
        background: "#ffffff",
        borderRadius: "8px",
        boxShadow: "0 4px 8px rgba(0,0,0,0.1)",
        padding: "20px",
      }}
    >
      <h3 style={{ marginBottom: "16px", color: "#00796b", fontSize: "18px", fontWeight: "bold" }}>
        {title}
      </h3>

      {items.map((item, index) => (
        <div key={index} style={{ marginBottom: "16px" }}>
          <div style={{ fontSize: "14px", marginBottom: "4px", color: "#555" }}>
            <span>
              {item.labelPrefix}
            </span>
            <span style={{ fontWeight: "bold" }}> 
              {item.labelSuffix}
            </span>
          </div>
          <div
            style={{
              height: "12px",
              background: "#e0e0e0",
              borderRadius: "6px",
              overflow: "hidden",
            }}
          >
            <div
              style={{
                height: "100%",
                width: `${item.percent}%`,
                background:  item.percent >= 80 ? "#4caf50" : item.percent >= 50 ? "#ff9800" : "#f44336",
                transition: "width 0.5s ease",
              }}
            />
          </div>
          <div style={{ fontSize: "12px", marginTop: "4px", color: "#333" }}>
            {typeof item.percent === "number"
              ? `${item.percent.toFixed(1)}%`
              : "Không có dữ liệu"}
          </div>
        </div>

      ))}
    </div>
  );
}

export default ProgressCard;
