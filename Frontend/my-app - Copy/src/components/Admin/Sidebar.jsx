export default function Sidebar({ activeTab, onChangeTab }) {
  const tabs = [
    { key: "hoso", label: "📁 Hồ sơ sức khỏe" },
    { key: "kham", label: "📋 Khám sức khỏe định kỳ" },
    { key: "theodoi", label: "📊 Theo dõi bệnh" },
    { key: "sucoyte", label: "⚕️ Sự cố y tế" },
    { key: "baohiem", label: "💳 Vaccine" },
    { key: "danhmuc", label: "📚 Danh mục" },
  ];

  return (
    <div className="sidebar">
      <h1>Y Tế</h1>
      <nav>
        {tabs.map((tab, idx) => (
          <div
            key={idx}
            onClick={() => onChangeTab(tab.key)}
            style={{
              padding: "10px 0",
              cursor: "pointer",
              fontWeight: activeTab === tab.key ? "bold" : "normal",
              color: activeTab === tab.key ? "#ffeb3b" : "#ffffff",
              borderBottom: activeTab === tab.key ? "2px solid #fff" : "none"
            }}
          >
            {tab.label}
          </div>
        ))}
      </nav>
    </div>
  );
}
