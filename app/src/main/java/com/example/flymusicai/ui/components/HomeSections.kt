package com.example.flymusicai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.flymusicai.R
import com.example.flymusicai.data.IndianMusicDatabase
import com.example.flymusicai.ui.theme.AmberGold
import com.example.flymusicai.ui.theme.TextPrimary
import com.example.flymusicai.ui.theme.TextSecondary

/**
 * 🎤 Popular Artists Section
 * Clean, circular artist avatars with names.
 */
@Composable
fun PopularArtistsSection(
    artists: List<IndianMusicDatabase.PopularArtist>,
    onArtistClick: (IndianMusicDatabase.PopularArtist) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Popular Singers",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = AmberGold
            )
            // Optional: "View All" text could go here
        }
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            items(artists) { artist ->
                ArtistItem(
                    artist = artist,
                    onClick = { onArtistClick(artist) }
                )
            }
        }
    }
}

@Composable
fun ArtistItem(
    artist: IndianMusicDatabase.PopularArtist,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(80.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = artist.imageUrl,
            contentDescription = artist.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape)
                .background(Color.DarkGray),
            placeholder = painterResource(R.drawable.music_placeholder),
            error = painterResource(R.drawable.music_placeholder)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = artist.name,
            fontSize = 12.sp,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = artist.genre,
            fontSize = 10.sp,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * 🎵 Popular Songs Section
 * Horizontal list of square song cards.
 */
@Composable
fun PopularSongsSection(
    songs: List<com.example.flymusicai.data.Music>,
    onSongClick: (com.example.flymusicai.data.Music) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "Popular Songs",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        )
        
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            items(songs) { song ->
                SongCard(
                    song = song,
                    onClick = { onSongClick(song) }
                )
            }
        }
    }
}

@Composable
fun SongCard(
    song: com.example.flymusicai.data.Music,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(140.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = song.coverImageUrl,
            contentDescription = song.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(140.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Color.DarkGray),
            placeholder = painterResource(R.drawable.music_placeholder),
            error = painterResource(R.drawable.music_placeholder)
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = song.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.SemiBold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        Text(
            text = song.artist,
            fontSize = 12.sp,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
