import React, { useState, useEffect } from "react";

const images = [
  "https://picsum.photos/id/1015/1200/400",
  "https://picsum.photos/id/1016/1200/400",
  "https://picsum.photos/id/1018/1200/400",
  "https://picsum.photos/id/1020/1200/400"
];

export default function Slider() {
  const [current, setCurrent] = useState(0);

  // Tự động chuyển slide sau 3s
  useEffect(() => {
    const timer = setInterval(() => {
      setCurrent(prev => (prev === images.length - 1 ? 0 : prev + 1));
    }, 3000);
    return () => clearInterval(timer);
  }, []);

  const prevSlide = () => {
    setCurrent(current === 0 ? images.length - 1 : current - 1);
  };

  const nextSlide = () => {
    setCurrent(current === images.length - 1 ? 0 : current + 1);
  };

  return (
    <div style={{ position: "relative", width: 1300, margin: "24px auto" }}>
      <img
        src={images[current]}
        alt={`slide-${current}`}
        style={{ width: "100%", borderRadius: 18, boxShadow: "0 2px 16px #bbb" }}
      />
      <button
        onClick={prevSlide}
        style={{
          position: "absolute", top: "50%", left: 20, transform: "translateY(-50%)",
          background: "#fff", border: "none", borderRadius: "50%", padding: 12, cursor: "pointer", fontSize: 22
        }}
      >&#8592;</button>
      <button
        onClick={nextSlide}
        style={{
          position: "absolute", top: "50%", right: 20, transform: "translateY(-50%)",
          background: "#fff", border: "none", borderRadius: "50%", padding: 12, cursor: "pointer", fontSize: 22
        }}
      >&#8594;</button>
      <div style={{ textAlign: "center", marginTop: 12 }}>
        {images.map((_, idx) => (
          <span
            key={idx}
            style={{
              display: "inline-block",
              width: 14, height: 14, borderRadius: "50%",
              background: idx === current ? "#1976d2" : "#ccc",
              margin: "0 6px"
            }}
          />
        ))}
      </div>
    </div>
  );
}