package com.coffeecat.animeplayer.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeecat.animeplayer.data.VideoInfo

@Composable
fun TopControl(video: VideoInfo,orientation:String,modifier: Modifier) {

    Box(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxHeight(0.2f)
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(Color(0xFF000000), Color(0x00000000))
                )
            ),
        contentAlignment = Alignment.TopStart
    ) {
        Text(
            text = video.title.substringBeforeLast(".", video.title),
            color = Color(0xFFEEEEEE),
            fontSize = 20.sp,
            maxLines = 1,
            modifier = Modifier.padding(start = 16.dp, top = 16.dp)
        )
    }
}
