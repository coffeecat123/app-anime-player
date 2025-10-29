package com.coffeecat.animeplayer.ui.screen

import android.app.Activity
import android.content.pm.ActivityInfo
import android.view.OrientationEventListener
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.OptIn
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.media3.common.util.Log
import androidx.media3.common.util.UnstableApi
import com.coffeecat.animeplayer.ui.component.FolderList
import com.coffeecat.animeplayer.ui.component.VideoPlayer
import com.coffeecat.animeplayer.viewmodel.MainViewModel

@Composable
fun MainScreen(viewModel: MainViewModel) {

    val context = LocalContext.current
    val activity = context as? Activity
    val uiState by viewModel.uiState.collectAsState()
    val folders = uiState.folders
    val currentVideo = uiState.currentVideo
    val isFullScreen = uiState.isFullScreen
    val canFullScreen = uiState.canFullScreen
    val nowOrientation = uiState.nowOrientation
    val configuration = LocalConfiguration.current

    LaunchedEffect(Unit) {
        viewModel.loadFolders(context)
    }

    // Launcher 必須在 Composable 中
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocumentTree()
    ) { uri ->
        uri?.let {
            viewModel.addFolder(context, it) // 呼叫 ViewModel 將資料加入 StateFlow + DataStore
        }
    }
    if(nowOrientation=="LANDSCAPE"){
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE
    }else{
        activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
    }
    DisposableEffect(Unit) {
        val listener = object : OrientationEventListener(context) {
            @OptIn(UnstableApi::class)
            override fun onOrientationChanged(angle: Int) {
                Log.d("VideoPlayer", currentVideo?.title.toString())
                if(currentVideo==null) return
                // angle: 0~359 度
                val t=25
                if (angle in 45+t..134-t || angle in 225+t..314-t) {
                    if (canFullScreen) {
                        viewModel.changeOrientation("LANDSCAPE")
                        viewModel.toggleFullScreen(true)
                    }
                } else {
                    if (!isFullScreen) {
                        viewModel.toggleCanFullScreen(true)
                        viewModel.changeOrientation("PORTRAIT")
                    }
                }
            }
        }
        listener.enable()

        onDispose { listener.disable() }
    }
    Column(modifier = Modifier.fillMaxSize()
        .background(Color(0xFF000000))
        .then(
            if (isFullScreen)
                Modifier
            else
                Modifier.windowInsetsPadding(WindowInsets.safeDrawing)
        )
    ) {

        Box(
            modifier =if (nowOrientation=="LANDSCAPE") {
                Modifier.fillMaxSize()
            } else {
                Modifier
                    .fillMaxWidth()
                    .aspectRatio(1920f / 1080f)
            }
        ) {
            if(nowOrientation=="LANDSCAPE"){
                BackHandler {
                    viewModel.changeOrientation("PORTRAIT")
                    viewModel.toggleCanFullScreen(false)
                    viewModel.toggleFullScreen(false)
                }
            }
            if (currentVideo!= null) {
                VideoPlayer(video = currentVideo!!, mainViewModel = viewModel)
                // DanmuLayer() 可以疊加在這裡
            } else {
                Text(
                    text = "請選擇影片",
                    color = Color.White,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }
        FolderList(
            folders = folders,
            onVideoClick = { video ->
                viewModel.selectVideo(video,context)
            },
            onAddFolder = {
                launcher.launch(null) // 由 Composable 負責觸發資料夾選擇
            }
        )
    }
}