package com.example.flymusicai.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.flymusicai.data.UserProfile
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/** ViewModel for handling authentication */
class AuthViewModel : ViewModel() {

    private val _isAuthenticated = MutableStateFlow(false)
    val isAuthenticated: StateFlow<Boolean> = _isAuthenticated.asStateFlow()

    private val _currentUser = MutableStateFlow<UserProfile?>(null)
    val currentUser: StateFlow<UserProfile?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // Simple in-memory storage for demo (use DataStore or Room in production)
    private val demoUsers = mutableMapOf<String, Pair<String, UserProfile>>()

    init {
        // Add a demo user
        demoUsers["demo@flymusicai.com"] =
                Pair(
                        "demo123",
                        UserProfile(
                                userId = "1",
                                username = "flymusicai_332_liha48",
                                email = "demo@flymusicai.com",
                                firstName = "Demo",
                                lastName = "User",
                                phoneNumber = "+91 XXXXXXXX10",
                                status = "Always listening to FlyMusic AI!",
                                dob = "01/01/2000",
                                gender = "Male",
                                isPremium = true,
                                profileImageUrl = "https://i.pravatar.cc/300?img=11"
                        )
                )
    }

    /** Login user */
    fun login(email: String, password: String) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            // Simulate network delay
            delay(1000)

            // Validate input
            if (email.isBlank() || password.isBlank()) {
                _authError.value = "Please fill in all fields"
                _isLoading.value = false
                return@launch
            }

            if (!isValidEmail(email)) {
                _authError.value = "Invalid email format"
                _isLoading.value = false
                return@launch
            }

            // Check credentials
            val userCredentials = demoUsers[email]
            if (userCredentials == null) {
                _authError.value = "User not found"
                _isLoading.value = false
                return@launch
            }

            if (userCredentials.first != password) {
                _authError.value = "Invalid password"
                _isLoading.value = false
                return@launch
            }

            // Login successful
            _currentUser.value = userCredentials.second
            _isAuthenticated.value = true
            _isLoading.value = false
        }
    }

    /** Sign up new user */
    fun signUp(
            username: String,
            email: String,
            password: String,
            confirmPassword: String,
            fullName: String = "",
            address: String = "",
            country: String = "",
            state: String = "",
            pincode: String = ""
    ) {
        viewModelScope.launch {
            _isLoading.value = true
            _authError.value = null

            // Simulate network delay
            delay(1000)

            // Validate input
            if (username.isBlank() || email.isBlank() || password.isBlank()) {
                _authError.value = "Please fill in all basic fields"
                _isLoading.value = false
                return@launch
            }

            if (!isValidEmail(email)) {
                _authError.value = "Invalid email format"
                _isLoading.value = false
                return@launch
            }

            if (password.length < 6) {
                _authError.value = "Password must be at least 6 characters"
                _isLoading.value = false
                return@launch
            }

            if (password != confirmPassword) {
                _authError.value = "Passwords do not match"
                _isLoading.value = false
                return@launch
            }

            // Check if user already exists
            if (demoUsers.containsKey(email)) {
                _authError.value = "User already exists"
                _isLoading.value = false
                return@launch
            }

            // Create new user with extra fields
            val newUser =
                    UserProfile(
                            userId = (demoUsers.size + 1).toString(),
                            username = username,
                            email = email,
                            firstName = username,
                            status = "Music Enthusiast"
                    )

            demoUsers[email] = Pair(password, newUser)

            // Auto-login after signup
            _currentUser.value = newUser
            _isAuthenticated.value = true
            _isLoading.value = false
        }
    }

    /** Logout user */
    fun logout() {
        _currentUser.value = null
        _isAuthenticated.value = false
        _authError.value = null
    }

    /** Update user profile */
    fun updateProfile(
            username: String,
            firstName: String,
            lastName: String,
            status: String,
            phoneNumber: String,
            email: String,
            dob: String,
            gender: String,
            profileImageUrl: String? = null
    ) {
        val current = _currentUser.value ?: return
        _currentUser.value =
                current.copy(
                        username = username,
                        firstName = firstName,
                        lastName = lastName,
                        status = status,
                        phoneNumber = phoneNumber,
                        email = email,
                        dob = dob,
                        gender = gender,
                        profileImageUrl = profileImageUrl ?: current.profileImageUrl
                )

        // Update in demo map as well to persist across mock sessions
        val entry = demoUsers[current.email]
        if (entry != null) {
            demoUsers[current.email] = entry.copy(second = _currentUser.value!!)
        }
    }

    /** Connect to Facebook */
    fun connectFacebook() {
        val current = _currentUser.value ?: return
        _currentUser.value = current.copy(isFacebookConnected = true)
    }

    /** Clear error message */
    fun clearError() {
        _authError.value = null
    }

    /** Validate email format */
    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
