package com.example.flymusicai.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.example.flymusicai.data.Playlist
import com.example.flymusicai.ui.theme.TextPrimary
import com.example.flymusicai.ui.theme.TextSecondary

/**
 * 🎵 Wide Playlist Card
 * Used for "Popular Albums" and other featured playlist sections.
 * Features a slightly wider aspect ratio or just a distinct style from song cards.
 */
@Composable
fun WidePlaylistCard(
    playlist: Playlist,
    onClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .width(160.dp) // Slightly wider than SongCard (140.dp)
            .clickable(onClick = onClick)
    ) {
        AsyncImage(
            model = playlist.coverImageUrl,
            contentDescription = playlist.name,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp) // Square for albums usually looks best, but let's stick to the container width
                .clip(RoundedCornerShape(12.dp))
                .background(Color.DarkGray),
            placeholder = painterResource(R.drawable.music_placeholder),
            error = painterResource(R.drawable.music_placeholder)
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = playlist.name,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = TextPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
        
        Text(
            text = playlist.description ?: "",
            fontSize = 13.sp,
            color = TextSecondary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}
