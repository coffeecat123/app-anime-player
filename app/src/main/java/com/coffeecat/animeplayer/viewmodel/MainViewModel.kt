package com.coffeecat.animeplayer.viewmodel

import android.content.Context
import android.content.Intent
import android.media.MediaMetadataRetriever
import android.net.Uri
import androidx.core.net.toUri
import androidx.datastore.preferences.core.edit
import androidx.documentfile.provider.DocumentFile
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.exoplayer.ExoPlayer
import com.coffeecat.animeplayer.data.FolderInfo
import com.coffeecat.animeplayer.data.VideoInfo
import com.coffeecat.animeplayer.utils.FOLDER_URIS
import com.coffeecat.animeplayer.utils.dataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(PlayerUiState())
    val uiState = _uiState.asStateFlow()

    var exoPlayer: ExoPlayer? = null
//        private set

    fun loadFolders(context: Context) {
        viewModelScope.launch {
            val folderUrisFlow: Flow<List<Uri>> = context.dataStore.data
                .map { prefs -> prefs[FOLDER_URIS]?.map { it.toUri() } ?: emptyList() }

            folderUrisFlow.collect { uris ->
                val folderList = uris.mapNotNull { uri ->
                    val doc = DocumentFile.fromTreeUri(context, uri) ?: return@mapNotNull null
                    val folderName = doc.name ?: "未知資料夾"

                    val videos = doc.listFiles()
                        .filter { it.isFile && it.name?.endsWith(".mp4") == true }
                        .map { file ->
                            val retriever = MediaMetadataRetriever()
                            retriever.setDataSource(context, file.uri)
                            val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
                            retriever.release()
                            VideoInfo(file.name ?: "未知影片", file.uri, duration)
                        }

                    FolderInfo(folderName, uri, videos)
                }
                _uiState.update { it.copy(folders = folderList) }
            }
        }
    }

    fun addFolder(context: Context, uri: Uri) {
        viewModelScope.launch {
            val doc = DocumentFile.fromTreeUri(context, uri) ?: return@launch
            val folderName = doc.name ?: "未知資料夾"

            if (_uiState.value.folders.any { it.name == folderName }) return@launch

            val videos = doc.listFiles()
                .filter { it.isFile && it.name?.endsWith(".mp4") == true }
                .map { file ->
                    val retriever = MediaMetadataRetriever()
                    retriever.setDataSource(context, file.uri)
                    val duration = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)?.toLong() ?: 0L
                    retriever.release()
                    VideoInfo(file.name ?: "未知影片", file.uri, duration)
                }

            if (videos.isEmpty()) return@launch

            val updatedFolders = _uiState.value.folders + FolderInfo(folderName, uri, videos)
            _uiState.update { it.copy(folders = updatedFolders) }

            context.contentResolver.takePersistableUriPermission(
                uri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )

            context.dataStore.edit { prefs ->
                val currentUris = prefs[FOLDER_URIS]?.toMutableSet() ?: mutableSetOf()
                currentUris.add(uri.toString())
                prefs[FOLDER_URIS] = currentUris
            }
        }
    }

    fun selectVideo(video: VideoInfo, context: Context) {
        if (_uiState.value.currentVideo == video) return
        _uiState.update { it.copy(currentVideo = video) }
        playVideo(context, video)
    }

    fun toggleFullScreen(a: Boolean? = null) {
        val newValue = when (a) {
            true -> true
            false -> false
            null -> !_uiState.value.isFullScreen
        }
        _uiState.update { it.copy(isFullScreen = newValue) }
    }

    fun toggleCanFullScreen(a: Boolean? = null) {
        val newValue = when (a) {
            true -> true
            false -> false
            null -> !_uiState.value.canFullScreen
        }
        _uiState.update { it.copy(canFullScreen = newValue) }
    }

    fun changeOrientation(a: String) {
        _uiState.update { it.copy(nowOrientation = a) }
    }

    private fun playVideo(context: Context, video: VideoInfo) {
        if (exoPlayer == null) {
            exoPlayer = ExoPlayer.Builder(context).build().apply { playWhenReady = true }
        }
        exoPlayer?.setMediaItem(MediaItem.fromUri(video.uri))
        exoPlayer?.prepare()
    }

    fun onFullScreenButtonClicked() {
        if (_uiState.value.nowOrientation == "LANDSCAPE") {
            _uiState.update {
                it.copy(
                    nowOrientation = "PORTRAIT",
                    canFullScreen = false,
                    isFullScreen = false
                )
            }
        } else {
            _uiState.update { it.copy(nowOrientation = "LANDSCAPE", isFullScreen = true) }
        }
    }

    override fun onCleared() {
        super.onCleared()
        exoPlayer?.release()
        exoPlayer = null
    }
}
