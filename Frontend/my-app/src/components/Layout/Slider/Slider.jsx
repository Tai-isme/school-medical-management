import React, { useState, useEffect } from "react";

const images = [
  "https://schoolmedicalmanagement.id.vn/assets/school3-uj2wFCjB.jpeg",
  "https://ydvn.edu.vn/wp-content/uploads/2022/05/1650057647099.jpeg",
  "https://giadinh.mediacdn.vn/2017/kham-benh-1512529403152.jpg",
  // Đổi ảnh thứ 4 thành ảnh mới
  "https://cdn.lawnet.vn/uploads/tintuc/2022/11/12/quyen-nghia-vu-nguoi-benh.jpg"
];

export default function Slider() {
  const [current, setCurrent] = useState(0);
  const [fade, setFade] = useState(true);

  useEffect(() => {
    const timer = setInterval(() => {
      setFade(false);
      setTimeout(() => {
        setCurrent(prev => (prev === images.length - 1 ? 0 : prev + 1));
        setFade(true);
      }, 350);
    }, 4000);
    return () => clearInterval(timer);
  }, []);

  const prevSlide = () => {
    setFade(false);
    setTimeout(() => {
      setCurrent(current === 0 ? images.length - 1 : current - 1);
      setFade(true);
    }, 350);
  };

  const nextSlide = () => {
    setFade(false);
    setTimeout(() => {
      setCurrent(current === images.length - 1 ? 0 : current + 1);
      setFade(true);
    }, 350);
  };

  return (
    <div
      style={{
        position: "relative",
        width: 1100,
        height: 400, // Thêm dòng này
        margin: "40px auto 20px auto",
        borderRadius: 32,
        overflow: "hidden",
        boxShadow: "0 8px 32px #1976d240",
        background: "#e3f2fd"
      }}
    >
      {/* Ảnh nền mờ */}
      <img
        src={images[current]}
        alt=""
        style={{
          position: "absolute",
          top: 0, left: 0, width: "100%", height: "100%",
          objectFit: "cover",
          filter: "blur(16px) brightness(0.7)",
          zIndex: 0,
          transition: "opacity 0.5s"
        }}
        aria-hidden
        onError={e => { e.target.src = "https://via.placeholder.com/1100x400?text=No+Image"; }}
      />
      {/* Ảnh chính */}
      <img
        src={images[current]}
        alt={`slide-${current}`}
        style={{
          width: "100%",
          height: 400, // Đặt chiều cao cố định
          objectFit: "cover", // Ảnh luôn lấp đầy khung
          borderRadius: 32,
          boxShadow: "0 4px 32px #1976d220",
          position: "relative",
          zIndex: 1,
          transition: "opacity 0.4s",
          opacity: fade ? 1 : 0,
          cursor: "pointer",
          transform: fade ? "scale(1)" : "scale(0.98)"
        }}
        onError={e => { e.target.src = "https://via.placeholder.com/1100x400?text=No+Image"; }}
      />
      {/* Nút chuyển */}
      <button
        onClick={prevSlide}
        style={{
          position: "absolute", top: "50%", left: 24, transform: "translateY(-50%)",
          background: "#fff", border: "none", borderRadius: "50%", padding: 14,
          cursor: "pointer", fontSize: 28, boxShadow: "0 2px 8px #1976d220",
          zIndex: 2, transition: "background 0.2s", display: "flex", alignItems: "center", justifyContent: "center"
        }}
        onMouseOver={e => (e.currentTarget.style.background = "#e3f2fd")}
        onMouseOut={e => (e.currentTarget.style.background = "#fff")}
        aria-label="Previous"
      >
        {/* Chevron SVG trái */}
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none">
          <circle cx="12" cy="12" r="12" fill="#e3f2fd" />
          <polyline points="14 8 10 12 14 16" stroke="#1976d2" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" fill="none"/>
        </svg>
      </button>
      <button
        onClick={nextSlide}
        style={{
          position: "absolute", top: "50%", right: 24, transform: "translateY(-50%)",
          background: "#fff", border: "none", borderRadius: "50%", padding: 14,
          cursor: "pointer", fontSize: 28, boxShadow: "0 2px 8px #1976d220",
          zIndex: 2, transition: "background 0.2s", display: "flex", alignItems: "center", justifyContent: "center"
        }}
        onMouseOver={e => (e.currentTarget.style.background = "#e3f2fd")}
        onMouseOut={e => (e.currentTarget.style.background = "#fff")}
        aria-label="Next"
      >
        {/* Chevron SVG phải */}
        <svg width="28" height="28" viewBox="0 0 24 24" fill="none">
          <circle cx="12" cy="12" r="12" fill="#e3f2fd" />
          <polyline points="10 8 14 12 10 16" stroke="#1976d2" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round" fill="none"/>
        </svg>
      </button>
      {/* Dots */}
      <div style={{ textAlign: "center", marginTop: 18, position: "relative", zIndex: 2 }}>
        {images.map((_, idx) => (
          <span
            key={idx}
            style={{
              display: "inline-block",
              width: 16, height: 16, borderRadius: "50%",
              background: idx === current ? "#1976d2" : "#bbb",
              margin: "0 7px",
              border: idx === current ? "2px solid #fff" : "none",
              boxShadow: idx === current ? "0 2px 8px #1976d220" : "none",
              transition: "background 0.3s, border 0.3s"
            }}
          />
        ))}
      </div>
    </div>
  );
}