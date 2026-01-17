package com.example.flymusicai.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
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

/** Favorites screen displaying user's favorite songs */
@Composable
fun FavoritesScreen(
        musicViewModel: MusicViewModel,
        onSongClick: (String) -> Unit,
        onNavigateToHome: () -> Unit = {}
) {
    val favoriteSongs by musicViewModel.favoriteSongs.collectAsState()

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
                text = "Favorites",
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onBackground
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (favoriteSongs.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                            imageVector = Icons.Default.Favorite,
                            contentDescription = "No favorites",
                            modifier = Modifier.size(80.dp),
                            tint = TextSecondary.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(text = "No favorite songs yet", color = TextSecondary, fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                            text = "Start adding songs to your favorites!",
                            color = TextSecondary.copy(alpha = 0.7f),
                            fontSize = 14.sp
                    )
                }
            }
        } else {
            Text(
                    text = "${favoriteSongs.size} favorite songs",
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
                items(favoriteSongs) { music ->
                    MusicCard(
                            music = music,
                            onClick = { onSongClick(music.id) },
                            isFavorite = true,
                            onFavoriteClick = { musicViewModel.toggleFavorite(music) }
                    )
                }
            }
        }
    }
}
