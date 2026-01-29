package com.example.flymusicai.ui.screens

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flymusicai.data.IndianMusicDatabase
import com.example.flymusicai.data.Music
import com.example.flymusicai.ui.theme.*
import com.example.flymusicai.viewmodel.MusicViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArtistDetailScreen(
    musicViewModel: MusicViewModel,
    artistName: String,
    onBack: () -> Unit,
    onSongClick: (String, List<Music>) -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val allMusic by musicViewModel.allMusic.collectAsState()
    val favoriteSongs by musicViewModel.favoriteSongs.collectAsState()
    
    // Bottom sheet state
    var selectedSongForOptions by remember { mutableStateOf<Music?>(null) }
    var showSongOptions by remember { mutableStateOf(false) }

    // Find artist details from static DB if possible, or infer
    val popularArtist = remember(artistName) {
        IndianMusicDatabase.popularArtists.find { it.name.equals(artistName, ignoreCase = true) }
    }
    
    // Live artist songs from network
    val artistSongsMap by musicViewModel.dynamicArtistSongsMap.collectAsState()
    
    // Trigger deep fetch for this artist if not already loaded or too few songs
    LaunchedEffect(artistName) {
        if ((artistSongsMap[artistName]?.size ?: 0) < 50) {
            musicViewModel.fetchArtistSongs(artistName)
        }
    }

    // Filter songs by artist & Merge with fetched results
    val mergedSongs = remember(artistName, allMusic, artistSongsMap) {
        val filtered = allMusic.filter { 
            it.artist.contains(artistName, ignoreCase = true) || 
            (popularArtist?.topSongs?.contains(it.title) == true)
        }
        val fetched = artistSongsMap[artistName] ?: emptyList()
        (filtered + fetched).distinctBy { it.id }.sortedByDescending { it.year }
    }
    
    val artistImage = popularArtist?.imageUrl ?: mergedSongs.firstOrNull()?.coverImageUrl ?: ""

    Scaffold(
        containerColor = DeepNavy,
        topBar = {
            TopAppBar(
                title = { Text(artistName, color = Color.White) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }
                },
                actions = {
                    IconButton(onClick = { /* Search in Artist */ }) {
                        Icon(Icons.Default.Search, contentDescription = "Search", tint = Color.White)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.Transparent)
            )
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 120.dp)
        ) {
            // Header Info (Like "Border 2" artifact)
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = artistImage,
                        contentDescription = artistName,
                        modifier = Modifier
                            .size(160.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .shadow(12.dp, RoundedCornerShape(12.dp), ambientColor = AmberGold, spotColor = AmberGold),
                        contentScale = ContentScale.Crop
                    )
                    
                    Spacer(modifier = Modifier.width(20.dp))
                    
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = artistName,
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            lineHeight = 30.sp
                        )
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text(
                            text = "${mergedSongs.size} songs",
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
                        onClick = { 
                            if (mergedSongs.isNotEmpty()) {
                                musicViewModel.playSong(mergedSongs.shuffled().first(), mergedSongs)
                            }
                        },
                        modifier = Modifier.weight(1f).height(48.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = AmberGold), 
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Icon(Icons.Default.Shuffle, contentDescription = null, tint = DeepNavy)
                        Spacer(Modifier.width(8.dp))
                        Text("Shuffle", color = DeepNavy, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                    }
                    
                    OutlinedButton(
                        onClick = { musicViewModel.startRadio(mergedSongs.firstOrNull() ?: return@OutlinedButton) },
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
            itemsIndexed(mergedSongs) { index, song ->
                SongListItem(
                    index = index + 1,
                    song = song,
                    onClick = { onSongClick(song.id, mergedSongs) },
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
        // Reuse existing bottom sheet component if available, using full package path to avoid import issues
        com.example.flymusicai.ui.components.SongOptionsBottomSheet(
            song = song,
            onDismiss = { showSongOptions = false },
            onPlayNext = { musicViewModel.addToPlayNext(it) },
            onAddToQueue = { musicViewModel.addToQueue(it) },
            onAddToPlaylist = { /* show playlist picker */ },
            onDownload = { musicViewModel.downloadSong(it) },
            onViewArtist = { /* Already here */ },
            onShare = { musicViewModel.shareSong(context, it) },
            onStartRadio = { musicViewModel.startRadio(it) },
            onAddToLibrary = { musicViewModel.toggleFavorite(it) },
            isFavorite = favoriteSongs.any { it.id == song.id },
            onToggleFavorite = { musicViewModel.toggleFavorite(it) }
        )
    }
}
