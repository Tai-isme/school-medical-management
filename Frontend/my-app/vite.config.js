import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [react()],
  // Thêm phần này để định nghĩa 'global' thành 'window'
  define: {
    global: 'window',
  },
  // Nếu bạn đang sử dụng biến môi trường cũ từ CRA (REACT_APP_...),
  // bạn cũng có thể cần định nghĩa process.env
  // process: {
  //   env: {} // Hoặc process.env: {} để nó không báo lỗi nếu thư viện nào đó dùng process.env
  // }
  // Tuy nhiên, tập trung vào `global` trước vì đó là lỗi hiện tại.
});