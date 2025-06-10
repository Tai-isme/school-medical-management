import { initializeApp } from 'firebase/app';
import { getAuth, GoogleAuthProvider } from 'firebase/auth';

// // Import the functions you need from the SDKs you need
// import { initializeApp } from "firebase/app";
// import { getAnalytics } from "firebase/analytics";
// // TODO: Add SDKs for Firebase products that you want to use
// // https://firebase.google.com/docs/web/setup#available-libraries

// // Your web app's Firebase configuration
// // For Firebase JS SDK v7.20.0 and later, measurementId is optional
// const firebaseConfig = {
//   apiKey: "AIzaSyC4-Xei9wPNL9G9osn1ARL8Ci4oqvVeZBM",
//   authDomain: "swp391-1861.firebaseapp.com",
//   projectId: "swp391-1861",
//   storageBucket: "swp391-1861.firebasestorage.app",
//   messagingSenderId: "304501138575",
//   appId: "1:304501138575:web:ebbde319578219d94bfcfa",
//   measurementId: "G-JV9EE6QC7K"
// };

// // Initialize Firebase
// const app = initializeApp(firebaseConfig);
// const analytics = getAnalytics(app);

const firebaseConfig = {
  apiKey: "AIzaSyAh0iFi1UFc3HQjb23tTqtcf35ZKcTcPL0",
      authDomain: "spring-boot-cc766.firebaseapp.com",
      projectId: "spring-boot-cc766",
      storageBucket: "spring-boot-cc766.firebasestorage.app",
      messagingSenderId: "40091313454",
      appId: "1:40091313454:web:6896cdbe3a4d57b52486bf",
      measurementId: "G-KXJLSC1DFE"
};

const app = initializeApp(firebaseConfig);
const auth = getAuth(app);
const googleProvider = new GoogleAuthProvider();

export { auth, googleProvider };