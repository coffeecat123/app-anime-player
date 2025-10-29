package com.coffeecat.animeplayer.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Slider
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.media3.common.C
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Fullscreen
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import com.coffeecat.animeplayer.viewmodel.MainViewModel
import kotlinx.coroutines.isActive

@Composable
fun ControlBar(
    mainViewModel: MainViewModel,
    orientation:String,
    modifier: Modifier = Modifier
) {

    val exoPlayer = mainViewModel.exoPlayer ?: return

    var isPlaying by remember { mutableStateOf(exoPlayer.isPlaying) }
    var currentPosition by remember { mutableStateOf(0f) }
    var duration by remember { mutableStateOf(0f) }

    // 每 200ms 更新一次進度
    LaunchedEffect(exoPlayer) {
        while (this.isActive) {
            if (exoPlayer.duration != C.TIME_UNSET) {
                duration = exoPlayer.duration.toFloat()
            }
            currentPosition = exoPlayer.currentPosition.toFloat()
            isPlaying = exoPlayer.isPlaying
            kotlinx.coroutines.delay(200)
        }
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0x00000000), Color(0xFF000000))
                )
            )
            .fillMaxHeight(0.2f),
        contentAlignment = Alignment.BottomCenter
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
                .padding(bottom = 8.dp)
        ) {
            // 播放 / 暫停按鈕
            IconButton(onClick = {
                if(exoPlayer.isPlaying) exoPlayer.pause() else exoPlayer.play()
            }) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = "Play/Pause",
                    tint = Color.White
                )
            }

            // 進度條
            if (duration > 0f) {
                Slider(
                    value = currentPosition.coerceIn(0f, duration),
                    onValueChange = { newValue ->
                        currentPosition = newValue
                        exoPlayer.seekTo(newValue.toLong())
                    },
                    valueRange = 0f..duration,
                    modifier = Modifier.weight(1f).padding(horizontal = 8.dp)
                )
            }

            // 全螢幕按鈕
            IconButton(onClick = { mainViewModel.onFullScreenButtonClicked() }) {
                Icon(
                    imageVector = Icons.Default.Fullscreen,
                    contentDescription = "FullScreen",
                    tint = Color.White
                )
            }
        }
    }
}
