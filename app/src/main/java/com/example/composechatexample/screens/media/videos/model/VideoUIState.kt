package com.example.composechatexample.screens.media.videos.model

import android.net.Uri
import androidx.media3.common.MediaItem

data class VideoUIState(
    val viewedItem: VideoItem? = null,
    val shouldShowControls: Boolean = false,
    val isPlaying: Boolean = false,
    val totalDuration: Long = 0L,
    val currentTime: Long = 0L,
    val bufferedPercentage: Int = 0,
    val playbackState: Int? = null,
    val fullScreen: Boolean = false,

    val uuid: String = "",
    val mediaId: String = "",
    val type: String = "",

    val title: String = "",
    val description: String = ""
)

data class VideoItem(
    val contentUri: Uri,
    val mediaItem: MediaItem
)

data class VideoDescription(
    val id: String,
    val name: String,
    val description: String,
)

