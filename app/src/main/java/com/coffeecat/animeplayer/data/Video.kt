package com.coffeecat.animeplayer.data

import android.net.Uri


data class VideoInfo(
    val title: String,
    val uri: Uri,
    val duration: Long
)