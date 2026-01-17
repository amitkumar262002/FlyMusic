// Firebase configuration
const firebaseConfig = {
  apiKey: "AIzaSyBGz_JOm8niyiOwIrW0xcKTLtPBb9ohgUA",
  authDomain: "fly-music-2fef0.firebaseapp.com",
  projectId: "fly-music-2fef0",
  storageBucket: "fly-music-2fef0.firebasestorage.app",
  messagingSenderId: "912339564501",
  appId: "1:912339564501:web:1c7d7ab76ff83b5d996f5c"
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);

// Initialize Firebase services
const auth = firebase.auth();
const db = firebase.firestore();

// Configure Google Auth Provider
const googleProvider = new firebase.auth.GoogleAuthProvider();
googleProvider.addScope('email');
googleProvider.addScope('profile');

// Export for use in other files
window.firebaseServices = {
  auth,
  db,
  googleProvider,
  firebase
};
