package com.example.flymusicai.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.flymusicai.data.Music
import com.example.flymusicai.ui.theme.*

data class ShareOption(
        val id: String,
        val title: String,
        val subtitle: String,
        val icon: ImageVector,
        val color: Color
)

/** 📤 Share Screen - Share songs with multiple options */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareScreen(
        song: Music,
        onBack: () -> Unit,
        onShareOption: (String) -> Unit,
        modifier: Modifier = Modifier
) {
        val shareOptions = remember {
                listOf(
                        ShareOption(
                                "whatsapp",
                                "WhatsApp",
                                "Share with contacts",
                                Icons.AutoMirrored.Filled.Chat,
                                Color(0xFF25D366)
                        ),
                        ShareOption(
                                "instagram",
                                "Instagram Story",
                                "Share to your story",
                                Icons.Default.CameraAlt,
                                Color(0xFFE1306C)
                        ),
                        ShareOption(
                                "facebook",
                                "Facebook",
                                "Post on your timeline",
                                Icons.Default.Facebook,
                                Color(0xFF1877F2)
                        ),
                        ShareOption(
                                "twitter",
                                "Twitter / X",
                                "Tweet this song",
                                Icons.Default.Tag,
                                Color(0xFF1DA1F2)
                        ),
                        ShareOption(
                                "telegram",
                                "Telegram",
                                "Send via Telegram",
                                Icons.AutoMirrored.Filled.Send,
                                Color(0xFF0088CC)
                        ),
                        ShareOption(
                                "copy_link",
                                "Copy Link",
                                "Copy song link",
                                Icons.Default.Link,
                                GoldAccent
                        ),
                        ShareOption(
                                "qr_code",
                                "QR Code",
                                "Generate QR code",
                                Icons.Default.QrCode,
                                TealAccent
                        ),
                        ShareOption(
                                "more",
                                "More Options",
                                "Other apps",
                                Icons.Default.MoreHoriz,
                                Color.White.copy(alpha = 0.7f)
                        )
                )
        }

        var showQRCode by remember { mutableStateOf(false) }
        var linkCopied by remember { mutableStateOf(false) }

        Scaffold(
                topBar = {
                        TopAppBar(
                                title = {
                                        Text(
                                                "Share",
                                                style = MaterialTheme.typography.titleLarge,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                        )
                                },
                                navigationIcon = {
                                        IconButton(onClick = onBack) {
                                                Icon(
                                                        Icons.Default.Close,
                                                        contentDescription = "Close",
                                                        tint = Color.White
                                                )
                                        }
                                },
                                colors =
                                        TopAppBarDefaults.topAppBarColors(containerColor = NavyBlue)
                        )
                },
                containerColor = NavyBlue
        ) { paddingValues ->
                LazyColumn(
                        modifier = modifier.fillMaxSize().padding(paddingValues),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        // Song Preview Card
                        item { SongPreviewCard(song) }

                        // Share Message with App Branding
                        item {
                                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                                "Share this amazing song with your friends!",
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = Color.White.copy(alpha = 0.7f)
                                        )
                                        
                                        // Share App Card
                                        Card(
                                                modifier = Modifier.fillMaxWidth().clickable {
                                                        onShareOption("share_app")
                                                },
                                                shape = RoundedCornerShape(12.dp),
                                                colors = CardDefaults.cardColors(
                                                        containerColor = AmberGold.copy(alpha = 0.15f)
                                                )
                                        ) {
                                                Row(
                                                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                        Icon(
                                                                Icons.Default.Share,
                                                                contentDescription = "Share FlyMusic AI",
                                                                tint = AmberGold,
                                                                modifier = Modifier.size(32.dp)
                                                        )
                                                        Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                        "Share FlyMusic AI App",
                                                                        color = Color.White,
                                                                        fontWeight = FontWeight.Bold,
                                                                        style = MaterialTheme.typography.bodyLarge
                                                                )
                                                                Text(
                                                                        "Tell your friends about this amazing music app!",
                                                                        color = Color.White.copy(alpha = 0.7f),
                                                                        style = MaterialTheme.typography.bodySmall
                                                                )
                                                        }
                                                        Icon(
                                                                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                                                contentDescription = null,
                                                                tint = AmberGold
                                                        )
                                                }
                                        }
                                }
                        }

                        // Share Options Grid
                        items(shareOptions.chunked(2)) { rowOptions ->
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        rowOptions.forEach { option ->
                                                ShareOptionCard(
                                                        option = option,
                                                        onClick = {
                                                                when (option.id) {
                                                                        "copy_link" -> {
                                                                                linkCopied = true
                                                                                onShareOption(
                                                                                        option.id
                                                                                )
                                                                        }
                                                                        "qr_code" -> {
                                                                                showQRCode = true
                                                                        }
                                                                        else ->
                                                                                onShareOption(
                                                                                        option.id
                                                                                )
                                                                }
                                                        },
                                                        modifier = Modifier.weight(1f)
                                                )
                                        }
                                        // Add spacer if odd number
                                        if (rowOptions.size == 1) {
                                                Spacer(modifier = Modifier.weight(1f))
                                        }
                                }
                        }

                        // Copy Success Message
                        item {
                                AnimatedVisibility(
                                        visible = linkCopied,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                ) {
                                        Card(
                                                modifier = Modifier.fillMaxWidth(),
                                                shape = RoundedCornerShape(12.dp),
                                                colors =
                                                        CardDefaults.cardColors(
                                                                containerColor =
                                                                        TealAccent.copy(
                                                                                alpha = 0.2f
                                                                        )
                                                        )
                                        ) {
                                                Row(
                                                        modifier =
                                                                Modifier.fillMaxWidth()
                                                                        .padding(16.dp),
                                                        verticalAlignment =
                                                                Alignment.CenterVertically,
                                                        horizontalArrangement =
                                                                Arrangement.spacedBy(12.dp)
                                                ) {
                                                        Icon(
                                                                Icons.Default.CheckCircle,
                                                                contentDescription = null,
                                                                tint = TealAccent
                                                        )
                                                        Text(
                                                                "Link copied to clipboard!",
                                                                color = Color.White,
                                                                fontWeight = FontWeight.SemiBold
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }

        // QR Code Dialog
        if (showQRCode) {
                QRCodeDialog(song = song, onDismiss = { showQRCode = false })
        }
}

@Composable
private fun SongPreviewCard(song: Music) {
        Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
        ) {
                Box(
                        modifier =
                                Modifier.fillMaxWidth()
                                        .background(
                                                Brush.horizontalGradient(
                                                        colors =
                                                                listOf(
                                                                        GoldAccent.copy(
                                                                                alpha = 0.2f
                                                                        ),
                                                                        TealAccent.copy(
                                                                                alpha = 0.2f
                                                                        )
                                                                )
                                                )
                                        )
                                        .padding(16.dp)
                ) {
                        Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                                // Album Art
                                AsyncImage(
                                        model = song.coverImageUrl,
                                        contentDescription = song.title,
                                        modifier =
                                                Modifier.size(80.dp).clip(RoundedCornerShape(12.dp))
                                )

                                // Song Info
                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                text = song.title,
                                                style = MaterialTheme.typography.titleMedium,
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White,
                                                maxLines = 2
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Text(
                                                text = song.artist,
                                                style = MaterialTheme.typography.bodyMedium,
                                                color = GoldAccent,
                                                maxLines = 1
                                        )

                                        Spacer(modifier = Modifier.height(4.dp))

                                        Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                                Icon(
                                                        Icons.Default.Album,
                                                        contentDescription = null,
                                                        modifier = Modifier.size(14.dp),
                                                        tint = Color.White.copy(alpha = 0.6f)
                                                )
                                                Text(
                                                        text = song.album,
                                                        style = MaterialTheme.typography.bodySmall,
                                                        color = Color.White.copy(alpha = 0.6f),
                                                        maxLines = 1
                                                )
                                        }
                                }
                        }
                }
        }
}

@Composable
private fun ShareOptionCard(
        option: ShareOption,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
) {
        Card(
                modifier = modifier.height(110.dp).clickable(onClick = onClick),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = DarkNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
                Column(
                        modifier = Modifier.fillMaxSize().padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                ) {
                        // Icon
                        Box(
                                modifier =
                                        Modifier.size(48.dp)
                                                .clip(CircleShape)
                                                .background(option.color.copy(alpha = 0.2f)),
                                contentAlignment = Alignment.Center
                        ) {
                                Icon(
                                        option.icon,
                                        contentDescription = null,
                                        tint = option.color,
                                        modifier = Modifier.size(24.dp)
                                )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Title
                        Text(
                                text = option.title,
                                style = MaterialTheme.typography.labelMedium,
                                fontWeight = FontWeight.SemiBold,
                                color = Color.White,
                                maxLines = 1
                        )

                        // Subtitle
                        Text(
                                text = option.subtitle,
                                style = MaterialTheme.typography.labelSmall,
                                color = Color.White.copy(alpha = 0.6f),
                                maxLines = 1
                        )
                }
        }
}

@Composable
private fun QRCodeDialog(song: Music, onDismiss: () -> Unit) {
        AlertDialog(
                onDismissRequest = onDismiss,
                title = {
                        Text(
                                "Scan to Listen",
                                fontWeight = FontWeight.Bold,
                                color = Color.White,
                                modifier = Modifier.fillMaxWidth(),
                                style = MaterialTheme.typography.titleLarge
                        )
                },
                text = {
                        Column(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                // QR Code Placeholder
                                Box(
                                        modifier =
                                                Modifier.size(200.dp)
                                                        .clip(RoundedCornerShape(16.dp))
                                                        .background(Color.White),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Column(
                                                horizontalAlignment = Alignment.CenterHorizontally,
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                                Icon(
                                                        Icons.Default.QrCode,
                                                        contentDescription = "QR Code",
                                                        modifier = Modifier.size(120.dp),
                                                        tint = NavyBlue
                                                )
                                                Text(
                                                        "QR Code",
                                                        color = NavyBlue,
                                                        fontWeight = FontWeight.Bold
                                                )
                                        }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                        song.title,
                                        style = MaterialTheme.typography.bodyMedium,
                                        fontWeight = FontWeight.SemiBold,
                                        color = Color.White,
                                        maxLines = 2
                                )

                                Text(
                                        song.artist,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = GoldAccent
                                )
                        }
                },
                confirmButton = {
                        Button(
                                onClick = onDismiss,
                                colors = ButtonDefaults.buttonColors(containerColor = GoldAccent)
                        ) { Text("Close", color = NavyBlue) }
                },
                containerColor = DarkNavy
        )
}
