package com.example.flymusicai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.flymusicai.R
import com.example.flymusicai.data.Music
import com.example.flymusicai.data.Playlist
import com.example.flymusicai.ui.theme.*
import com.example.flymusicai.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PlaylistDetailScreen(
    musicViewModel: MusicViewModel,
    playlistId: String,
    onBack: () -> Unit,
    onSongClick: (String) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val playlists by musicViewModel.playlists.collectAsState()
    val albumsForYou by musicViewModel.albumsForYou.collectAsState()
    val favoriteSongs by musicViewModel.favoriteSongs.collectAsState()
    
    // Bottom sheet state
    var selectedSongForOptions by remember { mutableStateOf<Music?>(null) }
    var showSongOptions by remember { mutableStateOf(false) }

    // Find playlist in regular playlists or dynamic albums
    val playlist = remember(playlistId, playlists, albumsForYou) {
        playlists.find { it.id == playlistId } ?: albumsForYou.find { it.id == playlistId }
    }

    LaunchedEffect(playlist) {
        if (playlist != null && playlist.songs.isEmpty()) {
            musicViewModel.fetchPlaylistSongs(playlist.id)
        }
    }

    Scaffold(
        containerColor = DeepNavy,
        topBar = {
            TopAppBar(
                title = { Text(playlist?.name ?: "Playlist", color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        if (playlist == null) {
            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Playlist not found", color = Color.White)
            }
            return@Scaffold
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header Info (Redesigned to match Image)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = playlist.coverImageUrl,
                        contentDescription = playlist.name,
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .shadow(12.dp, RoundedCornerShape(12.dp), ambientColor = AmberGold, spotColor = AmberGold),
                        contentScale = ContentScale.Crop,
                        placeholder = painterResource(R.drawable.music_placeholder),
                        error = painterResource(R.drawable.music_placeholder)
                    )
                    
                    Spacer(modifier = Modifier.width(20.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = playlist.name,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 30.sp
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "${playlist.songs.size} songs",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                        
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            IconButton(onClick = { /* Favorite */ }) {
                                Icon(Icons.Default.FavoriteBorder, contentDescription = null, tint = Color.White)
                            }
                            IconButton(onClick = { /* More */ }) {
                                Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.White)
                            }
                        }
                    }
                }
                
                // Action Buttons
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 20.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Button(
                        onClick = { musicViewModel.playPlaylist(playlist) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold), 
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(Icons.Default.Shuffle, contentDescription = null, tint = DeepNavy)
                        Spacer(Modifier.width(8.dp))
                        Text("Shuffle", color = DeepNavy, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    OutlinedButton(
                        onClick = { musicViewModel.playPlaylist(playlist) },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color.White),
                        border = ButtonDefaults.outlinedButtonBorder.copy(brush = SolidColor(Color.DarkGray)),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(Icons.Default.Radio, contentDescription = null)
                        Spacer(Modifier.width(8.dp))
                        Text("Radio", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
            
            // Song List
            itemsIndexed(playlist.songs) { index, song ->
                PlaylistSongListItem(
                    index = index + 1,
                    song = song,
                    onClick = { onSongClick(song.id) },
                    onMoreClick = {
                        selectedSongForOptions = song
                        showSongOptions = true
                    }
                )
            }
        }
    }

    // Options Bottom Sheet
    if (showSongOptions && selectedSongForOptions != null) {
        val song = selectedSongForOptions!!
        com.example.flymusicai.ui.components.SongOptionsBottomSheet(
            song = song,
            onDismiss = { showSongOptions = false },
            onPlayNext = { musicViewModel.addToPlayNext(it) },
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
}

@Composable
fun PlaylistSongListItem(index: Int, song: Music, onClick: () -> Unit, onMoreClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
            model = song.coverImageUrl.ifEmpty { "https://c.saavncdn.com/artists/${song.artist.replace(" ", "_")}_500x500.jpg" },
            contentDescription = null,
            modifier = Modifier
                .size(56.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop,
            placeholder = androidx.compose.ui.res.painterResource(id = com.example.flymusicai.R.drawable.music_placeholder),
            error = androidx.compose.ui.res.painterResource(id = com.example.flymusicai.R.drawable.music_placeholder)
        )
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = song.title,
                color = Color.White,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = song.artist,
                color = Color.Gray,
                fontSize = 13.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
        
        IconButton(onClick = onMoreClick) {
            Icon(Icons.Default.MoreVert, contentDescription = null, tint = Color.Gray)
        }
    }
}
