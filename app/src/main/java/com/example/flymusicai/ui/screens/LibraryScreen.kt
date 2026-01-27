package com.example.flymusicai.ui.screens

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.material3.ripple
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flymusicai.data.Music
import com.example.flymusicai.ui.theme.*
import com.example.flymusicai.data.ArtistConstants

// Data Models
data class LibraryMenuItem(
        val icon: ImageVector,
        val title: String,
        val count: Int? = null, // Optional count
        val showArrow: Boolean = true,
        val actionText: String? = null, // e.g., "Go Pro", "+ New"
        val actionColor: Color? = null,
        val onClick: () -> Unit = {}
)

data class HistoryItem(
        val id: String,
        val title: String,
        val timestamp: String,
        val imageUrl: String? = null // Placeholder for now
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LibraryScreen(
        musicViewModel: com.example.flymusicai.viewmodel.MusicViewModel,
        authViewModel: com.example.flymusicai.viewmodel.AuthViewModel,
        onSongClick: (Music) -> Unit = {},
        onNavigateToSettings: () -> Unit = {},
        onNavigateToEditProfile: () -> Unit = {},
        modifier: Modifier = Modifier
) {
    // Navigation & View State
    var currentSection by remember { mutableStateOf<String?>(null) }

    val currentUser by authViewModel.currentUser.collectAsState()
    val playlists by musicViewModel.playlists.collectAsState()
    val likedSongs by musicViewModel.favoriteSongs.collectAsState()
    val allSongs by musicViewModel.allMusic.collectAsState()
    val favoriteSongs by musicViewModel.favoriteSongs.collectAsState()

    // Bottom sheet state
    var selectedSongForOptions by remember { mutableStateOf<Music?>(null) }
    var showSongOptions by remember { mutableStateOf(false) }

    val context = androidx.compose.ui.platform.LocalContext.current

    // Playlist State (for "+ New")
    var showCreatePlaylistDialog by remember { mutableStateOf(false) }
    var newPlaylistName by remember { mutableStateOf("") }
    var newPlaylistImageUri by remember { mutableStateOf<String?>(null) }

    val photoPickerLauncherForPlaylist =
            rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.PickVisualMedia(),
                    onResult = { uri ->
                        if (uri != null) {
                            newPlaylistImageUri = uri.toString()
                        }
                    }
            )

    // Back Handler for internal navigation
    androidx.activity.compose.BackHandler(enabled = currentSection != null) {
        currentSection = null
    }

    Scaffold(
            modifier = modifier.fillMaxSize(),
            topBar = {
                CenterAlignedTopAppBar(
                        title = {
                            Text(
                                    currentSection ?: "My Library",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                            )
                        },
                        navigationIcon = {
                            if (currentSection != null) {
                                IconButton(onClick = { currentSection = null }) {
                                    Icon(
                                            Icons.AutoMirrored.Filled.ArrowBack,
                                            contentDescription = "Back",
                                            tint = Color.White
                                    )
                                }
                            }
                        },
                        actions = {
                            IconButton(onClick = onNavigateToSettings) {
                                Icon(
                                        Icons.Default.Settings,
                                        contentDescription = "Settings",
                                        tint = Color.White
                                )
                            }
                        },
                        colors =
                                TopAppBarDefaults.centerAlignedTopAppBarColors(
                                        containerColor = DeepNavy
                                )
                )
            },
            containerColor = Color.Black
    ) { innerPadding ->
        Box(
                modifier =
                        Modifier.fillMaxSize()
                                .background(Brush.verticalGradient(listOf(DeepNavy, Color.Black)))
        ) {

            // Main Content Area
            androidx.compose.animation.AnimatedContent(
                    targetState = currentSection,
                    label = "LibraryTransition"
            ) { section ->
                if (section == null) {
                    // *** ROOT LIBRARY VIEW ***
                    LazyColumn(
                            modifier = Modifier.fillMaxSize().padding(innerPadding),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // Profile
                        item {
                            ProfileSection(
                                    name = currentUser?.username ?: "Guest",
                                    bio = currentUser?.status ?: "Music Enthusiast",
                                    imageUrl = currentUser?.profileImageUrl
                                                    ?: "https://i.pravatar.cc/300?img=11",
                                    onEditClick = onNavigateToEditProfile
                            )
                        }

                        item { Spacer(modifier = Modifier.height(16.dp)) }

                        // Menu Items with "Glow" effect
                        val menuItems =
                                listOf(
                                        LibraryMenuItem(
                                                Icons.Default.Favorite,
                                                "Liked Songs",
                                                likedSongs.size,
                                                onClick = { currentSection = "Liked Songs" }
                                        ),
                                        LibraryMenuItem(
                                                Icons.Default.Album,
                                                "Albums",
                                                4,
                                                onClick = { currentSection = "Albums" }
                                        ),
                                        LibraryMenuItem(
                                                Icons.Default.Mic,
                                                "Artists",
                                                8,
                                                onClick = { currentSection = "Artists" }
                                        ),
                                        LibraryMenuItem(
                                                Icons.Default.Podcasts,
                                                "Shows",
                                                0,
                                                onClick = { currentSection = "Shows" }
                                        ),
                                        LibraryMenuItem(
                                                Icons.Default.Download,
                                                "Downloads",
                                                0, // Show count of downloaded songs
                                                onClick = { currentSection = "Downloads" }
                                        ),
                                        LibraryMenuItem(
                                                Icons.AutoMirrored.Filled.QueueMusic,
                                                "Playlists",
                                                playlists.size,
                                                actionText = "+ New",
                                                actionColor = AmberGold,
                                                onClick = { showCreatePlaylistDialog = true }
                                        ),
                                        LibraryMenuItem(
                                                Icons.Default.VideoLibrary,
                                                "Videos",
                                                2,
                                                onClick = { currentSection = "Videos" }
                                        )
                                )

                        items(menuItems) { item -> GlowLibraryItem(item) }

                        // Frequently Played Section
                        item {
                            Text(
                                    text = "Frequently Played",
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 18.sp,
                                    modifier = Modifier.padding(top = 24.dp, bottom = 12.dp)
                            )
                        }

                        items(allSongs.take(5)) { song ->
                            SongItem(
                                song = song, 
                                onClick = { onSongClick(song) },
                                onMoreClick = {
                                    selectedSongForOptions = song
                                    showSongOptions = true
                                }
                            )
                        }

                        item { Spacer(modifier = Modifier.height(80.dp)) }
                    }
                } else {
                    // *** SUB-SECTIONS VIEW ***
                    SubSectionView(
                            section = section,
                            musicViewModel = musicViewModel,
                            onSongClick = onSongClick,
                            onMoreClick = {
                                selectedSongForOptions = it
                                showSongOptions = true
                            },
                            padding = innerPadding
                    )
                }
            }
        }
    }

    // --- Song Options Bottom Sheet ---
    if (showSongOptions && selectedSongForOptions != null) {
        val song = selectedSongForOptions!!
        com.example.flymusicai.ui.components.SongOptionsBottomSheet(
            song = song,
            onDismiss = { showSongOptions = false },
            onPlayNext = { musicViewModel.playNext(it) },
            onAddToQueue = { musicViewModel.addToQueue(it) },
            onAddToPlaylist = { /* show playlist picker */ },
            onDownload = { /* musicViewModel.downloadSong(it) */ },
            onViewArtist = { /* Navigate to artist */ },
            onShare = { musicViewModel.shareSong(context, it) },
            onStartRadio = { musicViewModel.startRadio(it) },
            onAddToLibrary = { musicViewModel.toggleFavorite(it) },
            isFavorite = favoriteSongs.any { it.id == song.id },
            onToggleFavorite = { musicViewModel.toggleFavorite(it) }
        )
    }

    // --- Dialogs ---

    if (showCreatePlaylistDialog) {
        AlertDialog(
                onDismissRequest = { showCreatePlaylistDialog = false },
                containerColor = DeepNavy,
                title = { Text("New Playlist", color = AmberGold) },
                text = {
                    Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Box(
                                modifier =
                                        Modifier.size(100.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(Color(0xFF2C3E50))
                                                .clickable {
                                                    photoPickerLauncherForPlaylist.launch(
                                                            PickVisualMediaRequest(
                                                                    ActivityResultContracts
                                                                            .PickVisualMedia
                                                                            .ImageOnly
                                                            )
                                                    )
                                                },
                                contentAlignment = Alignment.Center
                        ) {
                            if (newPlaylistImageUri != null) {
                                AsyncImage(
                                        model = newPlaylistImageUri,
                                        contentDescription = null,
                                        modifier = Modifier.fillMaxSize(),
                                        contentScale = ContentScale.Crop
                                )
                            } else {
                                Icon(
                                        Icons.Default.AddPhotoAlternate,
                                        contentDescription = "Pick Image",
                                        tint = Color.Gray,
                                        modifier = Modifier.size(40.dp)
                                )
                            }
                        }

                        OutlinedTextField(
                                value = newPlaylistName,
                                onValueChange = { newPlaylistName = it },
                                label = { Text("Playlist Name", color = TextSecondary) },
                                colors =
                                        OutlinedTextFieldDefaults.colors(
                                                focusedBorderColor = AmberGold,
                                                unfocusedBorderColor = TextSecondary,
                                                focusedTextColor = Color.White,
                                                unfocusedTextColor = Color.White
                                        )
                        )
                    }
                },
                confirmButton = {
                    Button(
                            onClick = {
                                if (newPlaylistName.isNotBlank()) {
                                    musicViewModel.createPlaylist(newPlaylistName)
                                    showCreatePlaylistDialog = false
                                    newPlaylistName = ""
                                    newPlaylistImageUri = null
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = AmberGold)
                    ) { Text("Create", color = DeepNavy) }
                },
        )
    }
}

// --- SUB-SECTIONS ---

@Composable
fun SubSectionView(
    section: String,
    musicViewModel: com.example.flymusicai.viewmodel.MusicViewModel,
    onSongClick: (Music) -> Unit,
    onMoreClick: (com.example.flymusicai.data.Music) -> Unit = {},
    padding: PaddingValues
) {
    val songs by musicViewModel.songs.collectAsState()
    val playlists by musicViewModel.playlists.collectAsState()
    val likedSongs by musicViewModel.favoriteSongs.collectAsState()

    LazyColumn(modifier = Modifier.fillMaxSize().padding(padding).padding(16.dp)) {
        when (section) {
            "Liked Songs" -> {
                items(likedSongs) { song ->
                    SongItem(
                        song = song,
                        onClick = { onSongClick(song) },
                        onMoreClick = { onMoreClick(song) }
                    )
                }
            }
            "Artists" -> {
                val artists = songs.map { it.artist }.distinct()
                items(artists) { artist ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp)
                            .clickable {},
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color.Gray),
                            contentAlignment = Alignment.Center
                        ) {
                            AsyncImage(
                                model = ArtistConstants.getArtistImage(artist),
                                contentDescription = null,
                                modifier = Modifier.fillMaxSize().clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            artist,
                            color = Color.White,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Icon(
                            Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = TextSecondary
                        )
                    }
                }
            }
            "Albums" -> {
                items(playlists) { playlist ->
                    Row(
                            modifier =
                                    Modifier.fillMaxWidth()
                                            .padding(vertical = 8.dp)
                                            .clickable { /* Navigate to Album */},
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                                model = playlist.coverImageUrl,
                                contentDescription = null,
                                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                                contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(
                                    playlist.name,
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 16.sp
                            )
                            Text("Artist Name • 2024", color = TextSecondary, fontSize = 12.sp)
                        }
                    }
                }
            }
            "Downloads" -> {
                // Show a "Go Pro" banner if not premium, or show downloads
                item {
                    Card(
                            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                            colors =
                                    CardDefaults.cardColors(
                                            containerColor = AmberGold.copy(alpha = 0.1f)
                                    ),
                            border =
                                    androidx.compose.foundation.BorderStroke(
                                            1.dp,
                                            AmberGold
                                    )
                    ) {
                        Row(
                                modifier = Modifier.padding(16.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(Icons.Default.WorkspacePremium, null, tint = AmberGold)
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                        "Offline Mode is for Pro users!",
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                )
                                Text(
                                        "Download songs and listen anywhere.",
                                        color = Color.White.copy(alpha = 0.7f),
                                        fontSize = 12.sp
                                )
                            }
                        }
                    }
                }

                items(songs.take(3)) { song ->
                    Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp).alpha(0.5f),
                            verticalAlignment = Alignment.CenterVertically
                    ) {
                        AsyncImage(
                                model = song.coverImageUrl,
                                contentDescription = null,
                                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(4.dp)),
                                contentScale = ContentScale.Crop
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Column {
                            Text(song.title, color = Color.White, fontWeight = FontWeight.Bold)
                            Text("Offline Download Paused", color = AmberGold, fontSize = 10.sp)
                        }
                    }
                }
            }
            else -> {
                item {
                    Box(
                            modifier = Modifier.fillMaxSize().padding(top = 100.dp),
                            contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                    Icons.AutoMirrored.Filled.QueueMusic,
                                    null,
                                    tint = Color.Gray,
                                    modifier = Modifier.size(64.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text("No content in $section yet.", color = TextSecondary)
                        }
                    }
                }
            }
        }
    }
}

// --- COMPONENTS ---

@Composable
fun ProfileSection(name: String, bio: String, imageUrl: String, onEditClick: () -> Unit) {
    Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar
        Box(
                modifier =
                        Modifier.size(70.dp).clip(CircleShape).border(2.dp, AmberGold, CircleShape)
        ) {
            AsyncImage(
                    model = imageUrl,
                    contentDescription = null,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(name, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 20.sp)
            Spacer(modifier = Modifier.height(4.dp))
            Text(bio, color = AmberGold, fontSize = 12.sp)
        }

        TextButton(onClick = onEditClick) {
            Text("Edit", color = AmberGold, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun GlowLibraryItem(item: LibraryMenuItem) {
    // Interaction Source for "Hover/Press" effect
    val interactionSource = remember {
        androidx.compose.foundation.interaction.MutableInteractionSource()
    }
    val isPressed by interactionSource.collectIsPressedAsState()

    val backgroundColor = if (isPressed) Color(0xFF1E3246) else Color.Transparent
    val glowColor = if (isPressed) AmberGold.copy(alpha = 0.2f) else Color.Transparent

    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(8.dp))
                            .background(backgroundColor)
                            .border(1.dp, glowColor, RoundedCornerShape(8.dp))
                            .clickable(
                                    interactionSource = interactionSource,
                                    indication = ripple(color = AmberGold)
                            ) { item.onClick() }
                            .padding(vertical = 14.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
                item.icon,
                contentDescription = null,
                tint = if (isPressed) AmberGold else Color.White,
                modifier = Modifier.size(26.dp)
        )

        Spacer(modifier = Modifier.width(20.dp))

        Text(
                item.title,
                color = if (isPressed) AmberGold else Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.weight(1f)
        )

        // Action Button
        if (item.actionText != null && item.actionColor != null) {
            Surface(
                    color = item.actionColor.copy(alpha = 0.1f),
                    shape = RoundedCornerShape(4.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, item.actionColor)
            ) {
                Text(
                        item.actionText,
                        color = item.actionColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Count
        if (item.count != null && item.actionText == null) {
            Text(item.count.toString(), color = TextSecondary, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
        }

        Icon(
                Icons.AutoMirrored.Filled.KeyboardArrowRight,
                contentDescription = null,
                tint = TextSecondary,
                modifier = Modifier.size(20.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun centerAlignedTopBar(
        title: @Composable () -> Unit,
        actions: @Composable RowScope.() -> Unit,
        colors: TopAppBarColors
) {
    CenterAlignedTopAppBar(title = title, actions = actions, colors = colors)
}

@Composable
fun ProfileSection() {
    Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        // Avatar Placeholder
        Box(
                modifier = Modifier.size(50.dp).clip(CircleShape).background(Color(0xFF333333)),
                contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.Person, contentDescription = null, tint = Color.Gray) }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                    "+XXXXXXXX5332",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Surface(color = Color(0xFF333333), shape = RoundedCornerShape(4.dp)) {
                Text(
                        "Basic",
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 10.sp,
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
        }

        TextButton(onClick = { /* Edit Profile */}) {
            Text("Edit", color = AmberGold) // Yellow/Gold
        }
    }
}

@Composable
fun LibraryMenuItemRow(item: LibraryMenuItem) {
    Row(
            modifier =
                    Modifier.fillMaxWidth().clickable { item.onClick() }.padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
                item.icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Text(item.title, color = Color.White, fontSize = 16.sp, modifier = Modifier.weight(1f))

        // Action Button (Go Pro / + New)
        if (item.actionText != null && item.actionColor != null) {
            Surface(color = item.actionColor, shape = RoundedCornerShape(4.dp)) {
                Text(
                        item.actionText,
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        // Count
        if (item.count != null && item.actionText == null) {
            Text(item.count.toString(), color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
        } else if (item.count != null) {
            // For Playlist (0) shown after + New
            Text(item.count.toString(), color = Color.Gray, fontSize = 14.sp)
            Spacer(modifier = Modifier.width(8.dp))
        }

        if (item.showArrow) {
            Icon(
                    Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = Color.Gray,
                    modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun HistoryItemRow(item: HistoryItem) {
    Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        // Song Cover Placeholder
        Box(
                modifier =
                        Modifier.size(48.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(Color.DarkGray), // Placeholder color
                contentAlignment = Alignment.Center
        ) { Icon(Icons.Default.MusicNote, contentDescription = null, tint = Color.Gray) }

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(item.title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 14.sp)
            Text(item.timestamp, color = Color.Gray, fontSize = 12.sp)
        }

        IconButton(onClick = { /* Menu */}) {
            Icon(Icons.Default.MoreVert, contentDescription = "More", tint = Color.Gray)
        }
    }
}

@Composable
fun SongItem(song: Music, onClick: () -> Unit, onMoreClick: () -> Unit = {}) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .padding(vertical = 8.dp)
                            .clickable(
                                    onClick = onClick,
                                    interactionSource = remember { MutableInteractionSource() },
                                    indication = null
                            ),
            verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
                model = song.coverImageUrl,
                contentDescription = null,
                modifier = Modifier.size(50.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(song.title, color = Color.White, fontWeight = FontWeight.Bold, maxLines = 1)
            Text(song.artist, color = TextSecondary, fontSize = 12.sp, maxLines = 1)
        }
        IconButton(onClick = onMoreClick) {
            Icon(Icons.Default.MoreVert, contentDescription = null, tint = TextSecondary)
        }
    }
}
