import { getAuth, RecaptchaVerifier, signInWithPhoneNumber } from "firebase/auth";

// Khởi tạo Firebase Authentication (đảm bảo bạn đã cấu hình Firebase trong dự án của mình)
const auth = getAuth();

// Biến lưu trữ số điện thoại (bạn cần lấy số điện thoại từ người dùng)
const phoneNumber = "+84707333797"; // Thay bằng số điện thoại thực tế

window.recaptchaVerifier = new RecaptchaVerifier(auth, 'sign-in-button', {
  'size': 'invisible',
  'callback': (response) => {
    // Gọi API gửi OTP (phần này bạn cần tự triển khai logic gửi OTP đến số điện thoại)
    console.log("reCAPTCHA đã được xác minh.");
  }
});

const appVerifier = window.recaptchaVerifier;
signInWithPhoneNumber(auth, phoneNumber, appVerifier)
  .then((confirmationResult) => {
    window.confirmationResult = confirmationResult;
    console.log("Đã gửi mã OTP.");
    // Người dùng nhập OTP -> gọi confirmationResult.confirm(OTP)
    // Ví dụ (giả sử người dùng nhập OTP vào biến 'otp'):
    // const otp = prompt("Nhập mã OTP đã nhận:");
    // if (otp) {
    //   confirmationResult.confirm(otp).then((result) => {
    //     // User signed in successfully.
    //     const user = result.user;
    //     console.log("Xác thực thành công:", user);
    //     user.getIdToken().then((idToken) => {
    //       console.log("Firebase ID Token:", idToken);
    //       // Gửi ID Token này lên backend của bạn để xác thực
    //     });
    //   }).catch((error) => {
    //     console.error("Xác thực OTP thất bại:", error);
    //   });
    // }
  })
  .catch((error) => {
    console.error("Lỗi khi gửi OTP:", error);
  });