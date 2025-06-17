import React from 'react'; // Import React để viết component
import { Menu } from 'antd'; // Import component Menu từ Ant Design
import { // Import các biểu tượng từ Ant Design Icons
  FileTextOutlined,
  CalendarOutlined,
  BellOutlined,
  WarningOutlined,
  MedicineBoxOutlined,
  UnorderedListOutlined,
} from '@ant-design/icons';

const AppSidebar = ({ onMenuSelect, selectedMenu }) => { // Định nghĩa component functional AppSidebar với props là onMenuSelect và selectedMenu
  return (
    <div
      style={{
        height: "100vh",
        width: 220,
        background: "#008080",
        // XÓA hoặc COMMENT dòng position: "fixed"
        // position: "fixed",
        // top: 0,
        // left: 0,
      }}
    >
      <Menu
        mode="inline" // Chế độ hiển thị menu theo chiều dọc (các mục nằm trên một hàng)
        theme="light" // Sử dụng chủ đề "dark" (tối) của Ant Design. Điều này làm cho chữ và icon có màu trắng trên nền tối, tăng độ tương phản.
        selectedKeys={[selectedMenu]} // Đặt mục được chọn dựa trên giá trị của props selectedMenu
        onClick={({ key }) => onMenuSelect(key)} // Gọi hàm onMenuSelect được truyền từ cha khi người dùng click vào một mục menu, với tham số là key của mục đó
        style={{ // Các thuộc tính CSS để tùy chỉnh giao diện của Menu
          background: "#3399ff",
  height: "100%",
        }}
      >
        {/* Mục "Y Tế" - Tiêu đề của Sidebar */}
        <Menu.Item
          key="title" // Khóa định danh cho mục này
          disabled // Vô hiệu hóa mục này, không cho phép click vào
          style={{ // Các thuộc tính CSS cho tiêu đề "Y Tế"
            cursor: 'default',     // Đổi con trỏ chuột thành mặc định khi rê vào (vì bị disabled)
            fontWeight: 'bold',    // Chữ in đậm
            fontSize: '1.2em',     // Cỡ chữ lớn hơn
            color: '#fff',         // Màu chữ trắng
            height: 60,            // Chiều cao của vùng chứa "Y Tế"
            lineHeight: '60px'     // Căn giữa văn bản theo chiều dọc
          }}
        >
          Y Tế
        </Menu.Item>

        {/* Các mục Menu chính */}
        <Menu.Item key="1" icon={<FileTextOutlined />}> {/* Mục "Hồ sơ sức khỏe" với icon tài liệu */}
          Hồ sơ sức khỏe
        </Menu.Item>
        <Menu.Item key="2" icon={<CalendarOutlined />}> {/* Mục "Khám sức khỏe định kỳ" với icon lịch */}
          Khám sức khỏe định kỳ
        </Menu.Item>
        <Menu.Item key="3" icon={<BellOutlined />}> {/* Mục "Yêu cầu gọi thuốc" với icon chuông */}
          Yêu cầu gọi thuốc
        </Menu.Item>
        <Menu.Item key="4" icon={<WarningOutlined />}> {/* Mục "Sự cố y tế" với icon cảnh báo */}
          Sự cố y tế
        </Menu.Item>
        <Menu.Item key="5" icon={<MedicineBoxOutlined />}> {/* Mục "Vaccine" với icon hộp thuốc */}
          Vaccine
        </Menu.Item>
        <Menu.Item key="6" icon={<UnorderedListOutlined />}> {/* Mục "Danh mục" với icon danh sách */}
          Danh mục
        </Menu.Item>
      </Menu>
    </div>
  );
};

export default AppSidebar; // Xuất component để có thể sử dụng ở các file khác