package com.example.flymusicai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import com.example.flymusicai.R
import com.example.flymusicai.data.Music
import com.example.flymusicai.data.SearchCategories
import com.example.flymusicai.ui.components.VoiceSearchVisualizer
import com.example.flymusicai.ui.theme.*
import com.example.flymusicai.utils.CustomSpeechRecognizer
import com.example.flymusicai.viewmodel.MusicViewModel

@Composable
fun SearchScreen(
        musicViewModel: MusicViewModel,
        onSongClick: (String) -> Unit,
        onNavigateToHome: () -> Unit
) {
    // State
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by musicViewModel.searchResults.collectAsState()
    val searchSuggestions by musicViewModel.searchSuggestions.collectAsState()
    val favoriteSongs by musicViewModel.favoriteSongs.collectAsState()

    // Bottom sheet state
    var selectedSongForOptions by remember { mutableStateOf<Music?>(null) }
    var showSongOptions by remember { mutableStateOf(false) }

    val context = LocalContext.current

    // Custom Voice Search
    val speechRecognizer = remember { CustomSpeechRecognizer(context) }
    val isListening by speechRecognizer.isListening.collectAsState()
    val recognizedText by speechRecognizer.recognizedText.collectAsState()
    val soundLevel by speechRecognizer.soundLevel.collectAsState()
    val partialResults by speechRecognizer.partialResults.collectAsState()
    val error by speechRecognizer.error.collectAsState()

    // Voice search visibility
    var showVoiceSearch by remember { mutableStateOf(false) }

    // Handle recognized text
    LaunchedEffect(recognizedText) {
        if (recognizedText.isNotEmpty()) {
            searchQuery = recognizedText
            musicViewModel.searchMusic(recognizedText)
            // Close voice search after a short delay
            kotlinx.coroutines.delay(500)
            showVoiceSearch = false
        }
    }

    // Cleanup
    DisposableEffect(Unit) {
        onDispose {
            speechRecognizer.destroy()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(modifier = Modifier.fillMaxSize().background(DeepNavy).statusBarsPadding()) {
            // Search Header
            Column(modifier = Modifier.fillMaxWidth().background(DeepNavy)) {
                Row(
                        modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateToHome) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back", tint = Color.White)
                    }

                    if (searchQuery.isEmpty()) {
                        Text(
                                text = "Mood and Genres",
                                color = Color.White,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }

                // Search Input Field
                TextField(
                        value = searchQuery,
                        onValueChange = {
                            searchQuery = it
                            if (it.isNotBlank()) {
                                musicViewModel.searchMusic(it)
                            } else {
                                musicViewModel.clearSearchSuggestions()
                            }
                        },
                        placeholder = { Text("Search songs, artists...", color = TextSecondary) },
                        modifier =
                                Modifier.fillMaxWidth()
                                        .padding(horizontal = 16.dp, vertical = 8.dp)
                                        .height(56.dp)
                                        .clip(RoundedCornerShape(28.dp))
                                        .background(NavyLight),
                        colors =
                                TextFieldDefaults.colors(
                                        focusedContainerColor = NavyLight,
                                        unfocusedContainerColor = NavyLight,
                                        focusedTextColor = Color.White,
                                        unfocusedTextColor = Color.White,
                                        cursorColor = AmberGold,
                                        focusedIndicatorColor = Color.Transparent,
                                        unfocusedIndicatorColor = Color.Transparent,
                                        disabledIndicatorColor = Color.Transparent
                                ),
                        leadingIcon = {
                            Icon(
                                    Icons.Default.Search,
                                    contentDescription = "Search",
                                    tint = TextSecondary
                            )
                        },
                        trailingIcon = {
                            if (searchQuery.isNotEmpty()) {
                                IconButton(
                                        onClick = {
                                            searchQuery = ""
                                            musicViewModel.clearSearchSuggestions()
                                        }
                                ) {
                                    Icon(
                                            Icons.Default.Close,
                                            contentDescription = "Clear",
                                            tint = TextSecondary
                                    )
                                }
                            } else {
                                IconButton(onClick = { 
                                    showVoiceSearch = true
                                    speechRecognizer.startListening()
                                }) {
                                    Icon(
                                            Icons.Default.Mic,
                                            contentDescription = "Voice Search",
                                            tint = AmberGold
                                    )
                                }
                            }
                        },
                        singleLine = true,
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                        keyboardActions =
                                KeyboardActions(
                                        onSearch = {
                                            if (searchQuery.isNotBlank())
                                                    musicViewModel.searchMusic(searchQuery)
                                        }
                                )
                )
            }

            // Content
            LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (searchQuery.isBlank()) {
                    // Recent Searches
                    if (searchSuggestions.isNotEmpty()) {
                        item {
                            Text(
                                    "Recent Searches",
                                    color = AmberGold,
                                    style = MaterialTheme.typography.titleMedium,
                                    modifier = Modifier.padding(bottom = 8.dp)
                            )
                        }
                        items(searchSuggestions) { suggestion ->
                            Row(
                                    modifier =
                                            Modifier.fillMaxWidth()
                                                    .clickable {
                                                        searchQuery = suggestion
                                                        musicViewModel.searchMusic(suggestion)
                                                    }
                                                    .padding(vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                        Icons.Default.Search,
                                        contentDescription = null,
                                        tint = TextSecondary,
                                        modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(16.dp))
                                Text(suggestion, color = Color.White, fontSize = 16.sp)
                            }
                            Divider(color = NavyLight)
                        }
                    }

                    // Moods & moments Section
                    item {
                        Text(
                                text = "Moods & moments",
                                color = AmberGold,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }

                    item {
                        CategoryGrid(
                                items = SearchCategories.moods,
                                onItemClick = { mood ->
                                    searchQuery = mood
                                    musicViewModel.searchMusic(mood)
                                }
                        )
                    }

                    // Genres Section
                    item {
                        Text(
                                text = "Genres",
                                color = AmberGold,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                modifier = Modifier.padding(vertical = 16.dp)
                        )
                    }

                    item {
                        CategoryGrid(
                                items = SearchCategories.genres,
                                onItemClick = { genre ->
                                    searchQuery = genre
                                    musicViewModel.searchMusic(genre)
                                }
                        )
                    }
                } else {
                    // Search Results
                    if (searchResults.isEmpty()) {
                        item {
                            Box(
                                    modifier = Modifier.fillMaxWidth().padding(top = 50.dp),
                                    contentAlignment = Alignment.Center
                            ) { Text("No results found for '$searchQuery'", color = TextSecondary) }
                        }
                    } else {
                        items(searchResults) { song ->
                            SearchResultItem(
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

        // Custom Voice Search Overlay
        if (showVoiceSearch) {
            VoiceSearchVisualizer(
                isListening = isListening,
                soundLevel = soundLevel,
                partialText = partialResults,
                recognizedText = recognizedText,
                error = error,
                onClose = {
                    showVoiceSearch = false
                    speechRecognizer.cancel()
                }
            )
        }
    }
}

@Composable
fun SearchResultItem(song: Music, onClick: () -> Unit, onMoreClick: () -> Unit = {}) {
    Row(
            modifier =
                    Modifier.fillMaxWidth()
                            .clip(RoundedCornerShape(12.dp))
                            .background(NavyLight.copy(alpha = 0.5f))
                            .clickable { onClick() }
                            .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(
                model = song.coverImageUrl.ifEmpty { "https://c.saavncdn.com/artists/${song.artist.replace(" ", "_")}_500x500.jpg" },
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(56.dp).clip(RoundedCornerShape(8.dp)),
                placeholder = androidx.compose.ui.res.painterResource(id = com.example.flymusicai.R.drawable.music_placeholder),
                error = androidx.compose.ui.res.painterResource(id = com.example.flymusicai.R.drawable.music_placeholder)
        )

        Spacer(modifier = Modifier.width(16.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                    text = song.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1
            )
            Text(
                    text = song.artist,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 1
            )
        }
        
        IconButton(onClick = onMoreClick) {
            Icon(
                imageVector = Icons.Default.MoreVert,
                contentDescription = "More",
                tint = TextSecondary
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CategoryGrid(items: List<String>, onItemClick: (String) -> Unit) {
    FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            maxItemsInEachRow = 2
    ) {
        items.forEach { item ->
            Box(
                    modifier =
                            Modifier.weight(1f)
                                    .height(56.dp)
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(NavyLight)
                                    .clickable { onItemClick(item) }
                                    .padding(horizontal = 16.dp),
                    contentAlignment = Alignment.CenterStart
            ) {
                Text(
                        text = item,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                )
            }
        }
        // If odd number of items, add a spacer to fill the last row
        if (items.size % 2 != 0) {
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}
