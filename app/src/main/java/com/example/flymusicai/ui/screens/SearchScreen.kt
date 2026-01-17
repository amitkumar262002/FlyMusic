package com.example.flymusicai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.flymusicai.ui.components.MusicCard
import com.example.flymusicai.ui.theme.*
import com.example.flymusicai.viewmodel.MusicViewModel

/** Search screen for finding music */
@Composable
fun SearchScreen(
        musicViewModel: MusicViewModel,
        onSongClick: (String) -> Unit,
        onNavigateToHome: () -> Unit = {}
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by musicViewModel.searchResults.collectAsState()
    val searchSuggestions by musicViewModel.searchSuggestions.collectAsState()
    var showSuggestions by remember { mutableStateOf(false) }

    // Load popular suggestions on first launch
    LaunchedEffect(Unit) { musicViewModel.loadPopularSuggestions() }

    Column(
            modifier =
                    Modifier.fillMaxSize()
                            .background(MaterialTheme.colorScheme.background)
                            .padding(16.dp)
    ) {
        // 🎯 Premium Animated Logo - tap to go to Home
        com.example.flymusicai.ui.components.CompactPremiumLogo(
                onClick = onNavigateToHome,
                modifier = Modifier.padding(bottom = 8.dp)
        )

        Text(
                text = "Search",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Search bar with suggestions
        Column {
            OutlinedTextField(
                    value = searchQuery,
                    onValueChange = {
                        searchQuery = it
                        showSuggestions = it.isNotEmpty()
                        // Real-time search
                        musicViewModel.searchMusic(it)
                        // Update suggestions as you type
                        if (it.isNotEmpty()) {
                            musicViewModel.updateSearchSuggestions(it)
                        } else {
                            musicViewModel.loadPopularSuggestions()
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Search songs, artists, moods...") },
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
                    },
                    trailingIcon = {
                        if (searchQuery.isNotEmpty()) {
                            IconButton(
                                    onClick = {
                                        searchQuery = ""
                                        showSuggestions = false
                                        musicViewModel.searchMusic("")
                                    }
                            ) {
                                Icon(
                                        imageVector = Icons.Default.Clear,
                                        contentDescription = "Clear"
                                )
                            }
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors =
                            OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = PrimaryPurple,
                                    unfocusedBorderColor = TextSecondary.copy(alpha = 0.5f)
                            ),
                    singleLine = true
            )

            // Suggestions dropdown
            if (searchSuggestions.isNotEmpty() && (showSuggestions || searchQuery.isEmpty())) {
                Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors =
                                CardDefaults.cardColors(
                                        containerColor = MaterialTheme.colorScheme.surface
                                ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(8.dp)) {
                        if (searchQuery.isEmpty()) {
                            Text(
                                    text = "🔥 Popular Searches",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryPurple,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        } else {
                            Text(
                                    text = "Suggestions",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = PrimaryCyan,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        searchSuggestions.take(8).forEach { suggestion ->
                            Row(
                                    modifier =
                                            Modifier.fillMaxWidth()
                                                    .clickable {
                                                        searchQuery = suggestion
                                                        showSuggestions = false
                                                        musicViewModel.searchMusic(suggestion)
                                                    }
                                                    .padding(horizontal = 8.dp, vertical = 12.dp),
                                    verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                        imageVector =
                                                if (searchQuery.isEmpty())
                                                        Icons.AutoMirrored.Filled.TrendingUp
                                                else Icons.Default.Search,
                                        contentDescription = null,
                                        tint =
                                                MaterialTheme.colorScheme.onSurface.copy(
                                                        alpha = 0.6f
                                                ),
                                        modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(12.dp))
                                Text(
                                        text = suggestion,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Search results
        if (searchQuery.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            modifier = Modifier.size(80.dp),
                            tint = TextSecondary.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                            text = "Search for your favorite music",
                            color = TextSecondary,
                            fontSize = 16.sp
                    )
                }
            }
        } else if (searchResults.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text(
                        text = "No results found for \"$searchQuery\"",
                        color = TextSecondary,
                        fontSize = 16.sp
                )
            }
        } else {
            Text(
                    text = "${searchResults.size} results found",
                    color = TextSecondary,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
            )

            LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(bottom = 100.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(searchResults) { music ->
                    MusicCard(
                            music = music,
                            onClick = { onSongClick(music.id) },
                            isFavorite = musicViewModel.isFavorite(music.id),
                            onFavoriteClick = { musicViewModel.toggleFavorite(music) }
                    )
                }
            }
        }
    }
}
