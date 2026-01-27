package com.example.flymusicai.ui.screens

import android.app.DatePickerDialog
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flymusicai.ui.theme.*
import com.example.flymusicai.viewmodel.AuthViewModel
import java.util.*

/**
 * 📝 Edit Profile Screen matching the provided screenshots. DeepNavy background, AmberGold accents,
 * and standard input fields.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(authViewModel: AuthViewModel, onBack: () -> Unit) {
    val currentUser by authViewModel.currentUser.collectAsState()

    // Local state for form fields
    var username by remember { mutableStateOf(currentUser?.username ?: "") }
    var firstName by remember { mutableStateOf(currentUser?.firstName ?: "") }
    var lastName by remember { mutableStateOf(currentUser?.lastName ?: "") }
    var status by remember { mutableStateOf(currentUser?.status ?: "") }
    var phoneNumber by remember { mutableStateOf(currentUser?.phoneNumber ?: "") }
    var email by remember { mutableStateOf(currentUser?.email ?: "") }
    var dob by remember { mutableStateOf(currentUser?.dob ?: "") }
    var gender by remember { mutableStateOf(currentUser?.gender ?: "Male") }
    var profileImageUrl by remember { mutableStateOf(currentUser?.profileImageUrl) }

    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Photo Picker Launcher
    val photoPickerLauncher =
            rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia(),
                    onResult = { uri ->
                        if (uri != null) {
                            profileImageUrl = uri.toString()
                        }
                    }
            )

    // Date Picker Dialog
    val calendar = Calendar.getInstance()
    val datePickerDialog =
            DatePickerDialog(
                    context,
                    { _, year, month, dayOfMonth -> dob = "$dayOfMonth/${month + 1}/$year" },
                    calendar.get(Calendar.YEAR),
                    calendar.get(Calendar.MONTH),
                    calendar.get(Calendar.DAY_OF_MONTH)
            )

    var showGenderMenu by remember { mutableStateOf(false) }

    Scaffold(
            topBar = {
                TopAppBar(
                        title = {
                            Text("Edit Profile", color = Color.White, fontWeight = FontWeight.Bold)
                        },
                        navigationIcon = {
                            IconButton(onClick = onBack) {
                                Icon(
                                        Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = Color.White
                                )
                            }
                        },
                        actions = {
                            TextButton(
                                    onClick = {
                                        authViewModel.updateProfile(
                                                username = username,
                                                firstName = firstName,
                                                lastName = lastName,
                                                status = status,
                                                phoneNumber = phoneNumber,
                                                email = email,
                                                dob = dob,
                                                gender = gender,
                                                profileImageUrl = profileImageUrl
                                        )
                                        onBack()
                                    }
                            ) {
                                Text(
                                        "Save",
                                        color = Color.Gray,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp
                                )
                            }
                        },
                        colors = TopAppBarDefaults.topAppBarColors(containerColor = DeepNavy)
                )
            },
            containerColor = DeepNavy
    ) { padding ->
        Column(
                modifier =
                        Modifier.fillMaxSize()
                                .padding(padding)
                                .verticalScroll(scrollState)
                                .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Profile Photo Section
            Box(
                    modifier =
                            Modifier.size(120.dp)
                                    .clip(CircleShape)
                                    .background(Color(0xFF2C3E50))
                                    .clickable {
                                        photoPickerLauncher.launch(
                                                PickVisualMediaRequest(
                                                        ActivityResultContracts.PickVisualMedia
                                                                .ImageOnly
                                                )
                                        )
                                    },
                    contentAlignment = Alignment.Center
            ) {
                if (profileImageUrl != null) {
                    AsyncImage(
                            model = profileImageUrl,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                            Icons.Default.Person,
                            contentDescription = null,
                            tint = Color.Gray,
                            modifier = Modifier.size(60.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))
            Text("Add profile photo same as Facebook", color = Color.Gray, fontSize = 12.sp)
            TextButton(
                    onClick = {
                        photoPickerLauncher.launch(
                                PickVisualMediaRequest(
                                        ActivityResultContracts.PickVisualMedia.ImageOnly
                                )
                        )
                    }
            ) { Text("Add Photo", color = Color(0xFF00E5FF), fontWeight = FontWeight.Bold) }

            Spacer(modifier = Modifier.height(16.dp))

            // Facebook Connect Button
            Button(
                    onClick = {
                        if (currentUser?.isFacebookConnected == false)
                                authViewModel.connectFacebook()
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors =
                            ButtonDefaults.buttonColors(
                                    containerColor =
                                            if (currentUser?.isFacebookConnected == true)
                                                    Color(0xFF00C853)
                                            else Color(0xFF1877F2)
                            ), // Facebook Blue or Success Green
                    shape = RoundedCornerShape(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                            if (currentUser?.isFacebookConnected == true) Icons.Default.CheckCircle
                            else Icons.Default.Facebook,
                            contentDescription = null,
                            tint = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                            if (currentUser?.isFacebookConnected == true) "Facebook Connected"
                            else "Connect to Facebook",
                            color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Username Field
            ProfileField(
                    label = "Username",
                    value = username,
                    onValueChange = { username = it },
                    trailingAction = "Verify",
                    onTrailingActionClick = { /* Verify Logic */},
                    helperText = "Your public handle, visible on your profile."
            )

            // First Name Field
            ProfileField(
                    label = "First Name",
                    value = firstName,
                    onValueChange = { firstName = it },
                    placeholder = "Add first name"
            )

            // Last Name Field
            ProfileField(
                    label = "Last Name",
                    value = lastName,
                    onValueChange = { lastName = it },
                    placeholder = "Add last name"
            )

            // Status Field
            ProfileField(
                    label = "Status",
                    value = status,
                    onValueChange = { if (it.length <= 60) status = it },
                    placeholder = "Add an interesting status",
                    helperText = "This status lets others know what you're up to.",
                    charCount = "${status.length}/60"
            )

            // Phone Number Field
            ProfileField(
                    label = "Phone Number",
                    value = phoneNumber,
                    onValueChange = { phoneNumber = it },
                    placeholder = "+XXXXXXXXXXXX"
            )

            // Email Field
            ProfileField(
                    label = "Email",
                    value = email,
                    onValueChange = { email = it },
                    placeholder = "Add email address"
            )

            // Date of Birth Field
            ProfileField(
                    label = "Date of Birth",
                    value = dob,
                    onValueChange = { dob = it },
                    placeholder = "Select date",
                    trailingIcon = Icons.Default.CalendarToday,
                    onTrailingIconClick = { datePickerDialog.show() }
            )

            // Gender Selector
            Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
                Text("Gender", color = Color.Gray, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Box(modifier = Modifier.fillMaxWidth()) {
                    Box(
                            modifier =
                                    Modifier.fillMaxWidth()
                                            .height(56.dp)
                                            .clip(RoundedCornerShape(8.dp))
                                            .background(Color(0xFF1E272E))
                                            .clickable { showGenderMenu = true }
                                            .padding(horizontal = 16.dp),
                            contentAlignment = Alignment.CenterStart
                    ) {
                        Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(gender, color = Color.White)
                            Icon(
                                    Icons.Default.ArrowDropDown,
                                    contentDescription = null,
                                    tint = Color.White
                            )
                        }
                    }

                    DropdownMenu(
                            expanded = showGenderMenu,
                            onDismissRequest = { showGenderMenu = false },
                            modifier = Modifier.background(Color(0xFF1E272E))
                    ) {
                        listOf("Male", "Female", "Other", "Prefer not to say").forEach { option ->
                            DropdownMenuItem(
                                    text = { Text(option, color = Color.White) },
                                    onClick = {
                                        gender = option
                                        showGenderMenu = false
                                    }
                            )
                        }
                    }
                }
            }
            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileField(
        label: String,
        value: String,
        onValueChange: (String) -> Unit,
        placeholder: String = "",
        helperText: String? = null,
        trailingAction: String? = null,
        onTrailingActionClick: () -> Unit = {},
        trailingIcon: ImageVector? = null,
        onTrailingIconClick: () -> Unit = {},
        charCount: String? = null
) {
    Column(modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text(label, color = Color.Gray, fontSize = 14.sp)
            if (charCount != null) {
                Text(charCount, color = Color.Gray, fontSize = 12.sp)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        OutlinedTextField(
                value = value,
                onValueChange = onValueChange,
                modifier =
                        Modifier.fillMaxWidth().clickable {
                            if (trailingIcon != null) onTrailingIconClick()
                        },
                placeholder = { Text(placeholder, color = Color.DarkGray) },
                trailingIcon = {
                    if (trailingAction != null) {
                        TextButton(onClick = onTrailingActionClick) {
                            Text(trailingAction, color = Color.Gray)
                        }
                    } else if (trailingIcon != null) {
                        IconButton(onClick = onTrailingIconClick) {
                            Icon(trailingIcon, contentDescription = null, tint = Color.White)
                        }
                    }
                },
                colors =
                        OutlinedTextFieldDefaults.colors(
                                focusedContainerColor = Color(0xFF1E272E),
                                unfocusedContainerColor = Color(0xFF1E272E),
                                focusedBorderColor = Color.Transparent,
                                unfocusedBorderColor = Color.Transparent,
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                        ),
                shape = RoundedCornerShape(8.dp),
                singleLine = true
        )
        if (helperText != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(helperText, color = Color.Gray, fontSize = 11.sp)
        }
    }
}
