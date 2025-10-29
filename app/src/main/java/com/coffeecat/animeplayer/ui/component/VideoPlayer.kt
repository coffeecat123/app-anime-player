package com.coffeecat.animeplayer.ui.component

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.media3.ui.PlayerView
import com.coffeecat.animeplayer.data.VideoInfo
import com.coffeecat.animeplayer.viewmodel.MainViewModel
@Composable
fun VideoPlayer(video: VideoInfo, mainViewModel: MainViewModel) {

    val exoPlayer = mainViewModel.exoPlayer
    val uiState by mainViewModel.uiState.collectAsState()
    val nowOrientation = uiState.nowOrientation
    if (exoPlayer != null) {
        Box(
            modifier = Modifier.fillMaxSize()
                .padding(0.dp)
        ) {
            AndroidView(
                factory = {
                    PlayerView(it).apply {
                        player = exoPlayer
                        useController = false
                    }
                },
                modifier = Modifier.align(Alignment.Center)
            )
            TopControl(
                video = video,
                orientation=nowOrientation,
                modifier = if(nowOrientation=="LANDSCAPE")
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(horizontal = 32.dp)
                else
                    Modifier
                        .align(Alignment.TopStart)
            )
            ControlBar(
                mainViewModel = mainViewModel,
                orientation=nowOrientation,
                modifier = if(nowOrientation=="LANDSCAPE")
                    Modifier
                        .align(Alignment.BottomCenter)
                        .padding(horizontal = 32.dp)
                    else
                    Modifier
                        .align(Alignment.BottomCenter)
            )
        }
    }
}