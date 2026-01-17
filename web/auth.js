// Authentication functionality
class AuthManager {
    constructor() {
        this.auth = window.firebaseServices.auth;
        this.db = window.firebaseServices.db;
        this.googleProvider = window.firebaseServices.googleProvider;
        this.currentUser = null;
        
        this.initializeAuth();
        this.setupEventListeners();
    }

    initializeAuth() {
        // Listen for auth state changes
        this.auth.onAuthStateChanged((user) => {
            if (user) {
                this.currentUser = user;
                this.handleAuthSuccess(user);
            } else {
                this.currentUser = null;
                this.handleAuthLogout();
            }
        });
    }

    setupEventListeners() {
        // Login form
        const loginForm = document.getElementById('loginForm');
        if (loginForm) {
            loginForm.addEventListener('submit', (e) => this.handleLogin(e));
        }

        // Signup form
        const signupForm = document.getElementById('signupForm');
        if (signupForm) {
            signupForm.addEventListener('submit', (e) => this.handleSignup(e));
        }

        // Forgot password form
        const forgotPasswordForm = document.getElementById('forgotPasswordForm');
        if (forgotPasswordForm) {
            forgotPasswordForm.addEventListener('submit', (e) => this.handleForgotPassword(e));
        }

        // Google Sign In buttons
        const googleSignInBtn = document.getElementById('googleSignIn');
        const googleSignUpBtn = document.getElementById('googleSignUp');
        
        if (googleSignInBtn) {
            googleSignInBtn.addEventListener('click', () => this.handleGoogleAuth());
        }
        
        if (googleSignUpBtn) {
            googleSignUpBtn.addEventListener('click', () => this.handleGoogleAuth());
        }
    }

    async handleLogin(e) {
        e.preventDefault();
        
        const email = document.getElementById('loginEmail').value;
        const password = document.getElementById('loginPassword').value;

        if (!this.validateEmail(email)) {
            this.showToast('Please enter a valid email address', 'error');
            return;
        }

        if (password.length < 6) {
            this.showToast('Password must be at least 6 characters', 'error');
            return;
        }

        try {
            this.showLoading(true);
            await this.auth.signInWithEmailAndPassword(email, password);
            this.showToast('Welcome back to FlyMusic AI!', 'success');
        } catch (error) {
            this.handleAuthError(error);
        } finally {
            this.showLoading(false);
        }
    }

    async handleSignup(e) {
        e.preventDefault();
        
        const name = document.getElementById('signupName').value;
        const email = document.getElementById('signupEmail').value;
        const password = document.getElementById('signupPassword').value;
        const confirmPassword = document.getElementById('confirmPassword').value;

        if (!name.trim()) {
            this.showToast('Please enter your full name', 'error');
            return;
        }

        if (!this.validateEmail(email)) {
            this.showToast('Please enter a valid email address', 'error');
            return;
        }

        if (password.length < 6) {
            this.showToast('Password must be at least 6 characters', 'error');
            return;
        }

        if (password !== confirmPassword) {
            this.showToast('Passwords do not match', 'error');
            return;
        }

        try {
            this.showLoading(true);
            const userCredential = await this.auth.createUserWithEmailAndPassword(email, password);
            
            // Update user profile
            await userCredential.user.updateProfile({
                displayName: name
            });

            // Save user data to Firestore
            await this.saveUserData(userCredential.user, { name, email });
            
            this.showToast('Account created successfully! Welcome to FlyMusic AI!', 'success');
        } catch (error) {
            this.handleAuthError(error);
        } finally {
            this.showLoading(false);
        }
    }

    async handleForgotPassword(e) {
        e.preventDefault();
        
        const email = document.getElementById('resetEmail').value;

        if (!this.validateEmail(email)) {
            this.showToast('Please enter a valid email address', 'error');
            return;
        }

        try {
            this.showLoading(true);
            await this.auth.sendPasswordResetEmail(email);
            this.showToast('Password reset email sent! Check your inbox.', 'success');
            showLogin();
        } catch (error) {
            this.handleAuthError(error);
        } finally {
            this.showLoading(false);
        }
    }

    async handleGoogleAuth() {
        try {
            this.showLoading(true);
            const result = await this.auth.signInWithPopup(this.googleProvider);
            
            // Save user data to Firestore
            await this.saveUserData(result.user, {
                name: result.user.displayName,
                email: result.user.email,
                photoURL: result.user.photoURL
            });
            
            this.showToast('Successfully signed in with Google!', 'success');
        } catch (error) {
            this.handleAuthError(error);
        } finally {
            this.showLoading(false);
        }
    }

    async saveUserData(user, userData) {
        try {
            const userRef = this.db.collection('users').doc(user.uid);
            const userDoc = await userRef.get();
            
            if (!userDoc.exists) {
                await userRef.set({
                    ...userData,
                    uid: user.uid,
                    createdAt: firebase.firestore.FieldValue.serverTimestamp(),
                    lastLoginAt: firebase.firestore.FieldValue.serverTimestamp(),
                    preferences: {
                        theme: 'light',
                        language: 'en',
                        autoplay: true,
                        notifications: true
                    },
                    playlists: [],
                    favorites: [],
                    recentlyPlayed: []
                });
            } else {
                await userRef.update({
                    lastLoginAt: firebase.firestore.FieldValue.serverTimestamp()
                });
            }
        } catch (error) {
            console.error('Error saving user data:', error);
        }
    }

    handleAuthSuccess(user) {
        // Show loading animation
        this.showAuthLoading();
        
        setTimeout(() => {
            // Hide auth container and show app
            document.getElementById('auth-container').classList.add('hidden');
            document.getElementById('app-container').classList.remove('hidden');
            
            // Update user info in header
            this.updateUserInfo(user);
            
            // Load user data
            this.loadUserData(user.uid);
            
            // Hide loading
            this.hideAuthLoading();
        }, 1500);
    }

    showAuthLoading() {
        const loadingOverlay = document.createElement('div');
        loadingOverlay.id = 'auth-loading';
        loadingOverlay.innerHTML = `
            <div class="auth-loading-content">
                <div class="auth-loading-spinner">
                    <i class="fas fa-music rotating-logo"></i>
                </div>
                <p>Setting up your music experience...</p>
            </div>
        `;
        loadingOverlay.style.cssText = `
            position: fixed;
            top: 0;
            left: 0;
            width: 100%;
            height: 100%;
            background: linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%);
            display: flex;
            align-items: center;
            justify-content: center;
            z-index: 10000;
            color: white;
            text-align: center;
        `;
        document.body.appendChild(loadingOverlay);
    }

    hideAuthLoading() {
        const loadingOverlay = document.getElementById('auth-loading');
        if (loadingOverlay) {
            loadingOverlay.remove();
        }
    }

    handleAuthLogout() {
        // Show auth container and hide app
        document.getElementById('auth-container').classList.remove('hidden');
        document.getElementById('app-container').classList.add('hidden');
        
        // Reset forms
        this.resetForms();
        
        // Show welcome message
        setTimeout(() => {
            this.showToast('Signed out successfully. Welcome back anytime!', 'info');
        }, 500);
    }

    updateUserInfo(user) {
        const userName = document.getElementById('userName');
        const userAvatar = document.getElementById('userAvatar');
        
        if (userName) {
            userName.textContent = user.displayName || user.email.split('@')[0];
        }
        
        if (userAvatar) {
            userAvatar.src = user.photoURL || `https://ui-avatars.com/api/?name=${encodeURIComponent(user.displayName || user.email)}&background=667eea&color=fff`;
        }
    }

    async loadUserData(uid) {
        try {
            const userDoc = await this.db.collection('users').doc(uid).get();
            if (userDoc.exists) {
                const userData = userDoc.data();
                // Load user preferences, playlists, etc.
                this.loadUserPlaylists(userData.playlists || []);
                this.loadUserFavorites(userData.favorites || []);
                this.loadRecentlyPlayed(userData.recentlyPlayed || []);
            }
        } catch (error) {
            // Silently handle offline mode - app works without Firebase
            console.log('App running in offline mode');
        }
    }

    loadUserPlaylists(playlists) {
        const playlistContainer = document.getElementById('userPlaylists');
        if (playlistContainer) {
            playlistContainer.innerHTML = '';
            playlists.forEach(playlist => {
                const playlistElement = document.createElement('div');
                playlistElement.className = 'playlist-item';
                playlistElement.innerHTML = `
                    <i class="fas fa-music"></i>
                    <span>${playlist.name}</span>
                `;
                playlistElement.addEventListener('click', () => this.loadPlaylist(playlist.id));
                playlistContainer.appendChild(playlistElement);
            });
        }
    }

    loadUserFavorites(favorites) {
        // Implementation for loading favorites
        console.log('Loading favorites:', favorites);
    }

    loadRecentlyPlayed(recentlyPlayed) {
        // Implementation for loading recently played
        console.log('Loading recently played:', recentlyPlayed);
    }

    async signOut() {
        try {
            await this.auth.signOut();
            this.showToast('Signed out successfully', 'info');
        } catch (error) {
            this.showToast('Error signing out', 'error');
        }
    }

    handleAuthError(error) {
        let message = 'An error occurred. Please try again.';
        
        switch (error.code) {
            case 'auth/user-not-found':
                message = 'No account found with this email address.';
                break;
            case 'auth/wrong-password':
                message = 'Incorrect password. Please try again.';
                break;
            case 'auth/email-already-in-use':
                message = 'An account with this email already exists.';
                break;
            case 'auth/weak-password':
                message = 'Password is too weak. Please choose a stronger password.';
                break;
            case 'auth/invalid-email':
                message = 'Invalid email address format.';
                break;
            case 'auth/popup-closed-by-user':
                message = 'Sign-in popup was closed. Please try again.';
                break;
            case 'auth/network-request-failed':
                message = 'Network error. Please check your connection.';
                break;
            default:
                message = error.message || message;
        }
        
        this.showToast(message, 'error');
    }

    validateEmail(email) {
        const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
        return emailRegex.test(email);
    }

    showLoading(show) {
        const authBtns = document.querySelectorAll('.auth-btn, .google-btn');
        authBtns.forEach(btn => {
            if (show) {
                btn.disabled = true;
                btn.style.opacity = '0.7';
                btn.innerHTML = '<i class="fas fa-spinner fa-spin"></i> Please wait...';
            } else {
                btn.disabled = false;
                btn.style.opacity = '1';
                // Reset button text based on button type
                if (btn.classList.contains('google-btn')) {
                    btn.innerHTML = '<i class="fab fa-google"></i> Continue with Google';
                } else {
                    // Reset to original text based on form
                    const form = btn.closest('form');
                    if (form && form.id === 'loginForm') {
                        btn.innerHTML = '<i class="fas fa-sign-in-alt"></i> Sign In';
                    } else if (form && form.id === 'signupForm') {
                        btn.innerHTML = '<i class="fas fa-user-plus"></i> Create Account';
                    } else if (form && form.id === 'forgotPasswordForm') {
                        btn.innerHTML = '<i class="fas fa-paper-plane"></i> Send Reset Link';
                    }
                }
            }
        });
    }

    resetForms() {
        const forms = document.querySelectorAll('form');
        forms.forEach(form => form.reset());
    }

    showToast(message, type = 'info') {
        const toastContainer = document.getElementById('toast-container');
        
        // Clear existing toasts of the same type and message to prevent duplicates
        const existingToasts = toastContainer.querySelectorAll('.toast');
        existingToasts.forEach(existingToast => {
            const existingMessage = existingToast.querySelector('span').textContent;
            if (existingMessage === message) {
                existingToast.remove();
            }
        });
        
        // Limit maximum number of toasts to 3
        const allToasts = toastContainer.querySelectorAll('.toast');
        if (allToasts.length >= 3) {
            allToasts[0].remove();
        }
        
        const toast = document.createElement('div');
        toast.className = `toast ${type}`;
        
        const icon = type === 'success' ? 'check-circle' : 
                    type === 'error' ? 'exclamation-circle' : 
                    'info-circle';
        
        toast.innerHTML = `
            <i class="fas fa-${icon}"></i>
            <span>${message}</span>
        `;
        
        // Add animation class
        toast.style.opacity = '0';
        toast.style.transform = 'translateX(100%)';
        toastContainer.appendChild(toast);
        
        // Animate in
        setTimeout(() => {
            toast.style.opacity = '1';
            toast.style.transform = 'translateX(0)';
            toast.style.transition = 'all 0.3s ease';
        }, 10);
        
        // Remove toast after 4 seconds
        setTimeout(() => {
            if (toast.parentNode) {
                toast.style.opacity = '0';
                toast.style.transform = 'translateX(100%)';
                setTimeout(() => {
                    if (toast.parentNode) {
                        toast.remove();
                    }
                }, 300);
            }
        }, 4000);
    }

    clearAllToasts() {
        const toastContainer = document.getElementById('toast-container');
        const toasts = toastContainer.querySelectorAll('.toast');
        toasts.forEach(toast => {
            toast.style.opacity = '0';
            toast.style.transform = 'translateX(100%)';
            setTimeout(() => {
                if (toast.parentNode) {
                    toast.remove();
                }
            }, 300);
        });
    }
}

// UI Helper Functions
function showLogin() {
    document.getElementById('login-form').classList.remove('hidden');
    document.getElementById('signup-form').classList.add('hidden');
    document.getElementById('forgot-password-form').classList.add('hidden');
}

function showSignup() {
    document.getElementById('login-form').classList.add('hidden');
    document.getElementById('signup-form').classList.remove('hidden');
    document.getElementById('forgot-password-form').classList.add('hidden');
}

function showForgotPassword() {
    document.getElementById('login-form').classList.add('hidden');
    document.getElementById('signup-form').classList.add('hidden');
    document.getElementById('forgot-password-form').classList.remove('hidden');
}

function togglePassword(inputId) {
    const input = document.getElementById(inputId);
    const icon = input.nextElementSibling;
    
    if (input.type === 'password') {
        input.type = 'text';
        icon.classList.remove('fa-eye');
        icon.classList.add('fa-eye-slash');
    } else {
        input.type = 'password';
        icon.classList.remove('fa-eye-slash');
        icon.classList.add('fa-eye');
    }
}

function signOut() {
    if (window.authManager) {
        window.authManager.signOut();
    }
}

// Initialize auth manager when DOM is loaded
document.addEventListener('DOMContentLoaded', () => {
    window.authManager = new AuthManager();
});
