import React from "react";

function ChartCard({ title, children }) {
  return (
    <div style={cardStyle}>
      {title && <h3 style={titleStyle}>{title}</h3>}
      {children}
    </div>
  );
}

const cardStyle = {
  background: "#ffffff",
  borderRadius: "12px",
  boxShadow: "0 4px 12px rgba(0,0,0,0.1)",
  padding: "20px",
  width: "100%",
  minHeight: "350px",
  boxSizing: "border-box",
};

const titleStyle = {
  fontSize: "18px",
  fontWeight: "600",
  color: "#00796b",
  marginBottom: "15px",
};

export default ChartCard;
