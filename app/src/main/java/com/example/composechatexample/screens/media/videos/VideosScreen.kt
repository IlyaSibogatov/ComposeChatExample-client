package com.example.composechatexample.screens.media.videos

import android.app.Activity
import android.content.pm.ActivityInfo
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.media3.common.Player
import androidx.media3.ui.PlayerView
import androidx.navigation.NavHostController
import com.example.composechatexample.screens.media.videos.model.VideoScreenEvent
import com.example.composechatexample.ui.theme.OnBackground
import com.example.composechatexample.utils.BackPressHandler
import com.example.composechatexample.utils.player.BottomControls
import com.example.composechatexample.utils.player.CenterControls
import com.example.composechatexample.utils.player.TopControls
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.collectLatest

@Composable
fun VideosScreen(
    navController: NavHostController,
    uuid: String? = null,
    mediaId: String? = null
) {
    val context = LocalContext.current
    val viewModel: VideosViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()
    val exoPlayer = viewModel.player

    BackPressHandler(onBackPressed = { viewModel.backPress() })

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable(
                onClick = viewModel::displayControl
            )
            .background(color = OnBackground),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            modifier = Modifier
                .fillMaxWidth(),
            factory = { context ->
                PlayerView(context).apply {
                    player = exoPlayer
                    useController = false
                }
            },
        )
        PlayerControls(
            modifier = Modifier.fillMaxSize(),
            title = { uiState.value.title },
            isVisible = { uiState.value.shouldShowControls },
            isPlaying = { uiState.value.isPlaying },
            onReplayClick = {
                exoPlayer.seekBack()
            },
            onForwardClick = {
                exoPlayer.seekForward()
            },
            onPauseToggle = { viewModel.playClick() },
            totalDuration = { uiState.value.totalDuration },
            currentTime = { uiState.value.currentTime },
            bufferedPercentage = { uiState.value.bufferedPercentage },
            playbackState = { uiState.value.playbackState ?: 0 },
            onSeekChanged = { timeMs: Float ->
                viewModel.displayControl(true)
                viewModel.player.seekTo(timeMs.toLong())
            },
            onBackClick = { viewModel.backPress() },
            openFullScreen = {
                viewModel.displayControl(true)
                viewModel.updateRotate()
            },
            fullScreen = { uiState.value.fullScreen }
        )
    }

    val lifeCycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifeCycleOwner) {
        val listener = object : Player.Listener {
            override fun onEvents(
                player: Player,
                events: Player.Events
            ) {
                super.onEvents(player, events)
                viewModel.apply {
                    setCurrentTime(player.currentPosition.coerceAtLeast(0L))
                    setPlaybackState(player.playbackState)
                }
            }
        }
        exoPlayer.addListener(listener)

        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_CREATE) {
                if (uuid != null && mediaId != null) {
                    viewModel.setParameters(uuid, mediaId)
                }
            }
            if (event == Lifecycle.Event.ON_PAUSE)
                viewModel.playClick(false)
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
            exoPlayer.removeListener(listener)
        }
    }
    LaunchedEffect(key1 = Unit) {
        while (true) {
            viewModel.apply {
                setTotalDuration(player.duration.coerceAtLeast(0L))
                setCurrentTime(player.currentPosition.coerceAtLeast(0L))
                setBufferPercentage(player.bufferedPercentage)
                delay(500L)
            }
        }
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.eventsFlow.collectLatest { value ->
            when (value) {
                is VideoScreenEvent.OnBackPressed -> {
                    if (uiState.value.fullScreen) viewModel.updateRotate()
                    else {
                        (context as Activity).requestedOrientation =
                            ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
                        navController.popBackStack()
                    }
                }
            }
        }
    }
    (context as Activity).requestedOrientation = if (uiState.value.fullScreen)
        ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE else ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun PlayerControls(
    modifier: Modifier = Modifier,
    title: () -> String,
    isVisible: () -> Boolean,
    isPlaying: () -> Boolean,
    totalDuration: () -> Long,
    currentTime: () -> Long,
    bufferedPercentage: () -> Int,
    playbackState: () -> Int,
    fullScreen: () -> Boolean,

    onReplayClick: () -> Unit,
    onForwardClick: () -> Unit,
    onPauseToggle: () -> Unit,
    onSeekChanged: (timeMs: Float) -> Unit,
    onBackClick: () -> Unit,
    openFullScreen: () -> Unit
) {
    val visible = remember(isVisible()) { isVisible() }

    AnimatedVisibility(
        modifier = modifier,
        visible = visible,
        enter = fadeIn(),
        exit = fadeOut()
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            TopControls(
                modifier = Modifier
                    .fillMaxWidth(),
                title = title,
                onBackClick = { onBackClick() }
            )
            CenterControls(
                modifier = Modifier
                    .fillMaxWidth(),
                isPlaying = isPlaying,
                onPlayToggle = { onPauseToggle() },
                onReplayClick = { onReplayClick() },
                onForwardClick = { onForwardClick() },
                playbackState = playbackState
            )
            BottomControls(
                modifier = Modifier
                    .fillMaxWidth()
                    .animateEnterExit(
                        enter =
                        slideInVertically(
                            initialOffsetY = { fullHeight: Int ->
                                fullHeight
                            }
                        ),
                        exit =
                        slideOutVertically(
                            targetOffsetY = { fullHeight: Int ->
                                fullHeight
                            }
                        )
                    ),
                totalDuration = totalDuration,
                currentTime = currentTime,
                bufferPercentage = bufferedPercentage,
                openFullScreen = { openFullScreen() },
                onSeekChanged = { onSeekChanged(it) },
                isFullScreen = fullScreen
            )
        }
    }
}