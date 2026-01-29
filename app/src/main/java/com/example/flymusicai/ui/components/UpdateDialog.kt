package com.example.flymusicai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.flymusicai.data.AppUpdateConfig
import com.example.flymusicai.ui.theme.AmberGold
import com.example.flymusicai.ui.theme.DeepNavy
import com.example.flymusicai.ui.theme.GradientStart
import com.example.flymusicai.ui.theme.PrimaryPurple

@Composable
fun UpdateDialog(
    config: AppUpdateConfig,
    onDismiss: () -> Unit
) {
    val uriHandler = LocalUriHandler.current

    Dialog(
        onDismissRequest = {
            if (config.show_later_button) {
                onDismiss()
            }
        },
        properties = DialogProperties(
            dismissOnBackPress = config.show_later_button,
            dismissOnClickOutside = config.show_later_button
        )
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = DeepNavy),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = CardDefaults.cardElevation(16.dp)
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header Icon
                Box(
                    modifier = Modifier
                        .size(72.dp)
                        .background(PrimaryPurple.copy(alpha = 0.2f), RoundedCornerShape(36.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.RocketLaunch, 
                        contentDescription = "Update",
                        tint = AmberGold,
                        modifier = Modifier.size(36.dp)
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Title
                Text(
                    text = config.title,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Version Badge
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = AmberGold.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = "Version ${config.version_name}",
                        color = AmberGold,
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Message
                Text(
                    text = config.message,
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.LightGray,
                    textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                    lineHeight = 22.sp
                )

                Spacer(modifier = Modifier.height(32.dp))

                // Buttons
                Column(modifier = Modifier.fillMaxWidth()) {
                    Button(
                        onClick = { uriHandler.openUri(config.update_link) },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = config.update_now_text,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }

                    if (config.show_later_button) {
                        Spacer(modifier = Modifier.height(12.dp))
                        TextButton(
                            onClick = onDismiss,
                            modifier = Modifier.fillMaxWidth().height(50.dp),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = config.later_text,
                                color = Color.White.copy(alpha = 0.7f),
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }
        }
    }
}
