package com.coffeecat.animeplayer.viewmodel

import com.coffeecat.animeplayer.data.FolderInfo
import com.coffeecat.animeplayer.data.VideoInfo

data class PlayerUiState(
    val folders: List<FolderInfo> = emptyList(),
    val currentVideo: VideoInfo? = null,
    val isFullScreen: Boolean = false,
    val canFullScreen: Boolean = false,
    val nowOrientation: String = "PORTRAIT"
)
