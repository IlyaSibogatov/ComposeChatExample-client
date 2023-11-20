package com.example.composechatexample.screens.media.videos

import android.media.metrics.PlaybackStateEvent.STATE_PAUSED
import androidx.core.net.toUri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import com.example.composechatexample.data.remote.MediaService
import com.example.composechatexample.screens.media.videos.model.VideoItem
import com.example.composechatexample.screens.media.videos.model.VideoScreenEvent
import com.example.composechatexample.screens.media.videos.model.VideoUIState
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.MediaType
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class VideosViewModel @Inject constructor(
    val player: Player,
    private val mediaService: MediaService,
) : ViewModel() {
    private val _uiState = MutableStateFlow(VideoUIState())
    val uiState: StateFlow<VideoUIState> = _uiState

    private val eventChannel = Channel<VideoScreenEvent>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    var hidePanelJob: Job? = null

    init {
        player.prepare()
        player.playWhenReady = true
    }

    fun setParameters(uuid: String, mediaId: String) {
        _uiState.value = uiState.value.copy(
            uuid = uuid,
            mediaId = mediaId,
            type = MediaType.VIDEO.value
        )
        getMediaDescription(uuid, mediaId, MediaType.VIDEO.value)
        addVideoUri(uuid, mediaId)
    }

    private fun addVideoUri(uuid: String, mediaId: String) {
        if (uiState.value.viewedItem == null) {
            val uri = Constants.BASE_URL + "/uploads/upload_videos/$uuid/${mediaId}.mp4"
            _uiState.value = uiState.value.copy(
                viewedItem = VideoItem(uri.toUri(), MediaItem.fromUri(uri))
            )
            player.addMediaItem(MediaItem.fromUri(uri))
            setVideo()
        }
    }

    private fun getMediaDescription(uuid: String, mediaId: String, type: String) {
        viewModelScope.launch(Dispatchers.IO) {
            mediaService.getMediaDescription(uuid, mediaId, type)?.let {
                _uiState.value = uiState.value.copy(
                    title = it.name,
                    description = it.description
                )
            }
        }
    }

    private fun setVideo() {
        player.setMediaItem(uiState.value.viewedItem?.mediaItem ?: return)
        _uiState.value = uiState.value.copy(
            isPlaying = true
        )
    }

    fun displayControl(show: Boolean? = null) {
        hidePanelJob?.cancel()
        val value = show ?: !uiState.value.shouldShowControls
        _uiState.value = uiState.value.copy(
            shouldShowControls = value
        )
        if (value) {
            hidePanelJob = viewModelScope.launch(Dispatchers.IO) {
                delay(3000L)
                _uiState.value = uiState.value.copy(
                    shouldShowControls = !uiState.value.shouldShowControls
                )
            }
        }
    }

    fun setTotalDuration(totalDuration: Long) {
        _uiState.value = uiState.value.copy(
            totalDuration = totalDuration
        )
    }

    fun setCurrentTime(currentTime: Long) {
        _uiState.value = uiState.value.copy(
            currentTime = currentTime
        )
    }

    fun setBufferPercentage(bufferedPercentage: Int) {
        _uiState.value = uiState.value.copy(
            bufferedPercentage = bufferedPercentage
        )
    }

    fun playClick(playing: Boolean? = null) {
        _uiState.value = uiState.value.copy(
            isPlaying = playing ?: !uiState.value.isPlaying
        )
        when {
            uiState.value.playbackState == STATE_PAUSED -> {
                player.seekTo(0L)
            }

            !uiState.value.isPlaying -> {
                player.pause()
            }

            else -> {
                player.play()
            }
        }
        displayControl(true)
    }

    fun setPlaybackState(playbackState: Int) {
        _uiState.value = uiState.value.copy(
            playbackState = playbackState
        )
        if (playbackState == STATE_PAUSED) {
            _uiState.value = uiState.value.copy(
                shouldShowControls = true
            )
            player.pause()
        }
    }

    fun updateRotate() {
        _uiState.value = uiState.value.copy(
            fullScreen = !uiState.value.fullScreen
        )
        displayControl(true)
    }

    override fun onCleared() {
        super.onCleared()
        player.release()
        hidePanelJob = null
    }

    fun backPress() {
        sendEvent(VideoScreenEvent.OnBackPressed())
    }

    private fun sendEvent(event: VideoScreenEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}