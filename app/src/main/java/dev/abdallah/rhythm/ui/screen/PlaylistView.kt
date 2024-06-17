package dev.abdallah.rhythm.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import dev.abdallah.rhythm.R
import dev.abdallah.rhythm.data.db.Playlist
import dev.abdallah.rhythm.data.db.Song
import dev.abdallah.rhythm.ui.theme.Background
import dev.abdallah.rhythm.ui.theme.Blue
import dev.abdallah.rhythm.ui.theme.Gray
import dev.abdallah.rhythm.ui.theme.SemiTransparent

@OptIn(ExperimentalGlideComposeApi::class)
@Composable
fun PlaylistView(
    playlist: Playlist,
    songs: List<Song>,
    nowPlaying: Song,
    onItemClick: (Int) -> Unit,
    onPlay: () -> Unit,
    onShuffle: () -> Unit,
    onBack: () -> Unit,
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Background),
    ) {
        item {
            val brush = Brush.verticalGradient(listOf(Gray, Background))
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(brush)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .padding(top = 56.dp)
                ) {
                    IconButton(
                        modifier = Modifier
                            .background(SemiTransparent, CircleShape)
                            .padding(12.dp)
                            .size(24.dp),
                        onClick = { onBack() }) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_arrow_back_ios_24),
                            contentDescription = "Back",
                            tint = Color.White,
                        )
                    }
                    Spacer(modifier = Modifier.weight(1f))
                    IconButton(
                        modifier = Modifier
                            .background(SemiTransparent, CircleShape)
                            .padding(12.dp)
                            .size(24.dp),
                        onClick = {  }) {
                        Icon(
                            painter = painterResource(id = R.drawable.round_more_vert_24),
                            contentDescription = "More options",
                            tint = Color.White,
                        )
                    }
                }
                GlideImage(
                    model = R.drawable.round_audiotrack_24,
                    contentDescription = "Playlist artwork",
                    modifier = Modifier
                        .padding(top = 24.dp)
                        .size(192.dp)
                        .align(Alignment.CenterHorizontally)
                )
                Text(
                    text = playlist.name,
                    modifier = Modifier
                        .padding(top = 36.dp)
                        .align(Alignment.CenterHorizontally),
                    fontSize = 18.sp,
                    color = Color.White,
                    fontWeight = FontWeight.W600
                )
                Text(
                    text = "${songs.size} Songs",
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .align(Alignment.CenterHorizontally),
                    fontSize = 14.sp,
                    color = Color.Gray,

                    )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 16.dp, end = 8.dp),
                        onClick = { onPlay() },
                        colors = ButtonColors(
                            containerColor = Gray,
                            contentColor = Blue,
                            disabledContentColor = Blue,
                            disabledContainerColor = Gray
                        )
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(24.dp),
                            painter = painterResource(id = R.drawable.play_arrow_24px),
                            contentDescription = "Play"
                        )
                        Text(
                            text = "Play",
                            color = Blue,
                            fontWeight = FontWeight.W600,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    Button(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp, end = 16.dp),
                        onClick = {
                            onShuffle()
                        },
                        colors = ButtonColors(
                            containerColor = Gray,
                            contentColor = Blue,
                            disabledContentColor = Blue,
                            disabledContainerColor = Gray
                        ),
                    ) {
                        Icon(
                            modifier = Modifier
                                .padding(end = 8.dp)
                                .size(24.dp),
                            painter = painterResource(id = R.drawable.shuffle_24px),
                            contentDescription = "Shuffle"
                        )
                        Text(
                            text = "Shuffle",
                            color = Blue,
                            fontWeight = FontWeight.W600,
                            fontSize = 16.sp,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                }
            }
        }
        items(songs.size, key = { songs[it].id }) {
            Song(
                songs = songs,
                position = it,
                onItemClick = onItemClick,
                nowPlaying = nowPlaying
            )
        }
    }
}