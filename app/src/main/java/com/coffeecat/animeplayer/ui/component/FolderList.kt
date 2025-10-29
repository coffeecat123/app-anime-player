package com.coffeecat.animeplayer.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.coffeecat.animeplayer.R
import com.coffeecat.animeplayer.data.FolderInfo
import com.coffeecat.animeplayer.data.VideoInfo


@Composable
fun FolderList(
    folders: List<FolderInfo>,
    onVideoClick: (VideoInfo) -> Unit,
    onAddFolder: () -> Unit
){
    var selectedFolder by remember { mutableStateOf<FolderInfo?>(null) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF222222))
            .padding(top = 16.dp)
    ) {
        Row (verticalAlignment = Alignment.CenterVertically){
            if(selectedFolder != null){
                IconButton(onClick = { selectedFolder = null }) {
                    Icon(painter = painterResource(id = R.drawable.arrow_back),
                        contentDescription = null,
                        tint =Color(0xFFEEEEEE))
                }
                Text("📁 ${selectedFolder!!.name}",
                    fontSize = 24.sp,
                    color = Color(0xFFEEEEEE))
            } else {
                Button(onClick = onAddFolder) {
                    Text("add folder", fontSize = 20.sp)
                }
            }
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize()
                .padding(bottom = 8.dp),
        ) {
            if(selectedFolder != null){
                items(selectedFolder!!.videos) { video ->
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 12.dp)
                    ) {
                        Button(
                            onClick = { onVideoClick(video) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text(
                                text = video.title,
                                fontSize = 20.sp,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth(),
                                overflow = TextOverflow.Ellipsis
                            )
                        }
                        Spacer(modifier = Modifier.height(8.dp).fillMaxWidth())  // 固定間距
                    }
                }
            } else {
                items(folders, key = { it.name }) { folder ->
                    Button(onClick = { selectedFolder = folder }) {
                        Text("📁 ${folder.name}", fontSize = 20.sp)
                    }
                }
            }
        }
    }
}