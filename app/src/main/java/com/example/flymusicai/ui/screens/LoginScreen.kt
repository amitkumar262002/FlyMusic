package com.example.flymusicai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.filled.Login
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flymusicai.R
import com.example.flymusicai.ui.theme.*
import com.example.flymusicai.viewmodel.AuthViewModel

/** 🔐 Premium Login Screen - Navy & Gold Professional Design */
@Composable
fun LoginScreen(
        authViewModel: AuthViewModel,
        onLoginSuccess: () -> Unit,
        onNavigateToSignUp: () -> Unit
) {
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var passwordVisible by remember { mutableStateOf(false) }

        val isLoading by authViewModel.isLoading.collectAsState()
        val authError by authViewModel.authError.collectAsState()
        val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

        LaunchedEffect(isAuthenticated) {
                if (isAuthenticated) {
                        onLoginSuccess()
                }
        }

        Box(modifier = Modifier.fillMaxSize().background(DeepNavy)) {
                // Decorative background elements
                Box(
                        modifier =
                                Modifier.size(400.dp)
                                        .offset(x = (-100).dp, y = (-100).dp)
                                        .alpha(0.1f)
                                        .background(AmberGold, shape = CircleShape)
                )

                Column(
                        modifier = Modifier.fillMaxSize().padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                ) {
                        // ✅ Premium Brand Logo
                        Box(
                                modifier =
                                        Modifier.size(120.dp)
                                                .clip(CircleShape)
                                                .background(NavySurface), // Removed padding(20.dp)
                                contentAlignment = Alignment.Center
                        ) {
                                Image(
                                        painter = painterResource(id = R.drawable.fly_music_logo),
                                        contentDescription = "FlyMusic AI Logo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale =
                                                ContentScale.Crop // Changed to Crop to fill gaps
                                )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                                text = "Welcome Back",
                                fontSize = 34.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberGold,
                                letterSpacing = 1.sp
                        )

                        Text(
                                text = "Elevate your audio experience",
                                fontSize = 14.sp,
                                color = TextTertiary,
                                modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(48.dp))

                        // Email field with Navy style
                        OutlinedTextField(
                                value = email,
                                onValueChange = {
                                        email = it
                                        authViewModel.clearError()
                                },
                                placeholder = { Text("Email Address", color = TextTertiary) },
                                leadingIcon = {
                                        Icon(
                                                Icons.Default.Email,
                                                contentDescription = "Email",
                                                tint = AmberGold
                                        )
                                },
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions =
                                        KeyboardOptions(keyboardType = KeyboardType.Email),
                                singleLine = true,
                                colors =
                                        OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AmberGold,
                                                unfocusedBorderColor = NavyLight,
                                                focusedContainerColor = NavySurface,
                                                unfocusedContainerColor = NavySurface,
                                                cursorColor = AmberGold,
                                                focusedTextColor = TextWhite,
                                                unfocusedTextColor = TextWhite
                                        )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Password field
                        OutlinedTextField(
                                value = password,
                                onValueChange = {
                                        password = it
                                        authViewModel.clearError()
                                },
                                placeholder = { Text("Password", color = TextTertiary) },
                                leadingIcon = {
                                        Icon(
                                                Icons.Default.Lock,
                                                contentDescription = "Password",
                                                tint = AmberGold
                                        )
                                },
                                trailingIcon = {
                                        IconButton(
                                                onClick = { passwordVisible = !passwordVisible }
                                        ) {
                                                Icon(
                                                        imageVector =
                                                                if (passwordVisible)
                                                                        Icons.Default.Visibility
                                                                else Icons.Default.VisibilityOff,
                                                        contentDescription =
                                                                if (passwordVisible) "Hide password"
                                                                else "Show password",
                                                        tint = TextTertiary
                                                )
                                        }
                                },
                                visualTransformation =
                                        if (passwordVisible) VisualTransformation.None
                                        else PasswordVisualTransformation(),
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions =
                                        KeyboardOptions(keyboardType = KeyboardType.Password),
                                singleLine = true,
                                colors =
                                        OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AmberGold,
                                                unfocusedBorderColor = NavyLight,
                                                focusedContainerColor = NavySurface,
                                                unfocusedContainerColor = NavySurface,
                                                cursorColor = AmberGold,
                                                focusedTextColor = TextWhite,
                                                unfocusedTextColor = TextWhite
                                        )
                        )

                        // Error message
                        if (authError != null) {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                        text = authError ?: "",
                                        color = Color.Red,
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Start,
                                        modifier = Modifier.fillMaxWidth()
                                )
                        }

                        // Forgot password
                        Text(
                                text = "Forgot Password?",
                                color = OrangeVibrant,
                                fontSize = 13.sp,
                                modifier =
                                        Modifier.align(Alignment.End)
                                                .padding(top = 12.dp)
                                                .clickable { /* Handle forgot password */},
                                fontWeight = FontWeight.SemiBold
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        // Premium Gold Login button
                        Button(
                                onClick = { authViewModel.login(email, password) },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = AmberGold,
                                                contentColor = DeepNavy
                                        ),
                                enabled = !isLoading && email.isNotBlank() && password.isNotBlank()
                        ) {
                                if (isLoading) {
                                        CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                color = DeepNavy,
                                                strokeWidth = 2.dp
                                        )
                                } else {
                                        Icon(Icons.Default.Login, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                                "LOGIN",
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 2.sp
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Sign up link
                        Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text(text = "Don't have an account? ", color = TextTertiary)
                                Text(
                                        text = "Sign Up",
                                        color = YellowSoft,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable { onNavigateToSignUp() }
                                )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        // Demo credentials box
                        Surface(
                                modifier = Modifier.fillMaxWidth(),
                                color = NavyLight.copy(alpha = 0.5f),
                                shape = RoundedCornerShape(12.dp),
                                border =
                                        androidx.compose.foundation.BorderStroke(
                                                1.dp,
                                                AmberGold.copy(alpha = 0.2f)
                                        )
                        ) {
                                Column(modifier = Modifier.padding(16.dp)) {
                                        Text(
                                                text = "Demo Credentials",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 12.sp,
                                                color = AmberGold
                                        )
                                        Text(
                                                text = "Email: demo@flymusicai.com",
                                                fontSize = 12.sp,
                                                color = TextWhite.copy(alpha = 0.7f),
                                                modifier = Modifier.padding(top = 4.dp)
                                        )
                                        Text(
                                                text = "Password: demo123",
                                                fontSize = 12.sp,
                                                color = TextWhite.copy(alpha = 0.7f)
                                        )
                                }
                        }
                }
        }
}
