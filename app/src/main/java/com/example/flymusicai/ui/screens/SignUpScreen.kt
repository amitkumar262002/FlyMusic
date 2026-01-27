package com.example.flymusicai.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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

/** 🚀 Premium Registration Screen - Navy & Gold Theme with Advanced Fields */
@Composable
fun SignUpScreen(
        authViewModel: AuthViewModel,
        onSignUpSuccess: () -> Unit,
        onNavigateToLogin: () -> Unit
) {
        // Basic Info
        var fullName by remember { mutableStateOf("") }
        var username by remember { mutableStateOf("") }
        var email by remember { mutableStateOf("") }
        var password by remember { mutableStateOf("") }
        var confirmPassword by remember { mutableStateOf("") }

        // Address Info
        var address by remember { mutableStateOf("") }
        var country by remember { mutableStateOf("") }
        var state by remember { mutableStateOf("") }
        var pincode by remember { mutableStateOf("") }

        // UI State
        var passwordVisible by remember { mutableStateOf(false) }
        var confirmPasswordVisible by remember { mutableStateOf(false) }

        val isLoading by authViewModel.isLoading.collectAsState()
        val authError by authViewModel.authError.collectAsState()
        val isAuthenticated by authViewModel.isAuthenticated.collectAsState()

        val scrollState = rememberScrollState()

        LaunchedEffect(isAuthenticated) {
                if (isAuthenticated) {
                        onSignUpSuccess()
                }
        }

        Box(modifier = Modifier.fillMaxSize().background(DeepNavy)) {
                // Decorative background elements
                Box(
                        modifier =
                                Modifier.size(300.dp)
                                        .offset(x = 200.dp, y = (-50).dp)
                                        .alpha(0.05f)
                                        .background(AmberGold, shape = CircleShape)
                )

                Column(
                        modifier =
                                Modifier.fillMaxSize()
                                        .verticalScroll(scrollState)
                                        .padding(horizontal = 24.dp, vertical = 40.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        // ✅ Premium Brand Logo
                        Box(
                                modifier =
                                        Modifier.size(100.dp)
                                                .clip(CircleShape)
                                                .background(NavySurface), // Removed padding(16.dp)
                                contentAlignment = Alignment.Center
                        ) {
                                Image(
                                        painter = painterResource(id = R.drawable.fly_music_logo),
                                        contentDescription = "Fly Music Logo",
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop // Changed to Crop
                                )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                                text = "Join Fly Music FlyAI",
                                fontSize = 32.sp,
                                fontWeight = FontWeight.Black,
                                color = AmberGold,
                                letterSpacing = 1.sp
                        )

                        Text(
                                text = "Create your premium account today",
                                fontSize = 14.sp,
                                color = TextTertiary,
                                modifier = Modifier.padding(top = 4.dp)
                        )

                        Spacer(modifier = Modifier.height(40.dp))

                        // 🛡️ Registration Form section
                        Text(
                                text = "PERSONAL INFORMATION",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberGold.copy(alpha = 0.7f),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        )

                        PremiumTextField(
                                value = fullName,
                                onValueChange = { fullName = it },
                                label = "Full Name",
                                icon = Icons.Default.Person
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PremiumTextField(
                                value = username,
                                onValueChange = { username = it },
                                label = "Username",
                                icon = Icons.Default.Badge
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PremiumTextField(
                                value = email,
                                onValueChange = { email = it },
                                label = "Email Address",
                                icon = Icons.Default.Email,
                                keyboardType = KeyboardType.Email
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PremiumPasswordField(
                                value = password,
                                onValueChange = { password = it },
                                label = "Password",
                                visible = passwordVisible,
                                onToggleVisibility = { passwordVisible = !passwordVisible }
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        PremiumPasswordField(
                                value = confirmPassword,
                                onValueChange = { confirmPassword = it },
                                label = "Confirm Password",
                                visible = confirmPasswordVisible,
                                onToggleVisibility = {
                                        confirmPasswordVisible = !confirmPasswordVisible
                                }
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // 📍 Address section
                        Text(
                                text = "ADDRESS DETAILS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = AmberGold.copy(alpha = 0.7f),
                                modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp)
                        )

                        PremiumTextField(
                                value = address,
                                onValueChange = { address = it },
                                label = "Street Address",
                                icon = Icons.Default.Home
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Row(modifier = Modifier.fillMaxWidth()) {
                                Box(modifier = Modifier.weight(1f)) {
                                        PremiumTextField(
                                                value = country,
                                                onValueChange = { country = it },
                                                label = "Country",
                                                icon = Icons.Default.Public
                                        )
                                }
                                Spacer(modifier = Modifier.width(16.dp))
                                Box(modifier = Modifier.weight(1f)) {
                                        PremiumTextField(
                                                value = state,
                                                onValueChange = { state = it },
                                                label = "State",
                                                icon = Icons.Default.LocationCity
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        PremiumTextField(
                                value = pincode,
                                onValueChange = { pincode = it },
                                label = "Pincode / Zip Code",
                                icon = Icons.Default.MarkunreadMailbox,
                                keyboardType = KeyboardType.Number
                        )

                        // Error message
                        if (authError != null) {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                        text = authError ?: "",
                                        color = Color.Red,
                                        style = MaterialTheme.typography.bodySmall,
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                )
                        }

                        Spacer(modifier = Modifier.height(40.dp))

                        // 🚀 Premium Sign Up Button
                        Button(
                                onClick = {
                                        authViewModel.signUp(
                                                username,
                                                email,
                                                password,
                                                confirmPassword,
                                                fullName,
                                                address,
                                                country,
                                                state,
                                                pincode
                                        )
                                },
                                modifier = Modifier.fillMaxWidth().height(56.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors =
                                        ButtonDefaults.buttonColors(
                                                containerColor = AmberGold,
                                                contentColor = DeepNavy
                                        ),
                                enabled =
                                        !isLoading &&
                                                email.isNotBlank() &&
                                                password.isNotBlank() &&
                                                fullName.isNotBlank()
                        ) {
                                if (isLoading) {
                                        CircularProgressIndicator(
                                                modifier = Modifier.size(24.dp),
                                                color = DeepNavy,
                                                strokeWidth = 2.dp
                                        )
                                } else {
                                        Icon(Icons.Default.PersonAdd, contentDescription = null)
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                                "CREATE ACCOUNT",
                                                fontWeight = FontWeight.ExtraBold,
                                                letterSpacing = 2.sp
                                        )
                                }
                        }

                        Spacer(modifier = Modifier.height(32.dp))

                        // Login link
                        Row(
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(bottom = 24.dp)
                        ) {
                                Text(text = "Already have an account? ", color = TextTertiary)
                                Text(
                                        text = "Login",
                                        color = YellowSoft,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable { onNavigateToLogin() }
                                )
                        }
                }
        }
}

/** Helper Composable for Premium TextFields */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumTextField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        icon: androidx.compose.ui.graphics.vector.ImageVector,
        keyboardType: KeyboardType = KeyboardType.Text
) {
        OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(label, color = TextTertiary, fontSize = 14.sp) },
                leadingIcon = {
                        Icon(
                                icon,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(20.dp)
                        )
                },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
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
}

/** Helper Composable for Premium PasswordFields */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PremiumPasswordField(
        value: String,
        onValueChange: (String) -> Unit,
        label: String,
        visible: Boolean,
        onToggleVisibility: () -> Unit
) {
        OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text(label, color = TextTertiary, fontSize = 14.sp) },
                leadingIcon = {
                        Icon(
                                Icons.Default.Lock,
                                contentDescription = null,
                                tint = AmberGold,
                                modifier = Modifier.size(20.dp)
                        )
                },
                trailingIcon = {
                        IconButton(onClick = onToggleVisibility) {
                                Icon(
                                        imageVector =
                                                if (visible) Icons.Default.Visibility
                                                else Icons.Default.VisibilityOff,
                                        contentDescription = null,
                                        tint = TextTertiary
                                )
                        }
                },
                visualTransformation =
                        if (visible) VisualTransformation.None else PasswordVisualTransformation(),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
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
}
