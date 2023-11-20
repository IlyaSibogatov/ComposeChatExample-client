package com.example.composechatexample.utils.player

import android.media.metrics.PlaybackStateEvent.STATE_PAUSED
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.composechatexample.R
import com.example.composechatexample.components.CustomIconButton

@Composable
fun CenterControls(
    modifier: Modifier = Modifier,
    isPlaying: () -> Boolean,
    playbackState: () -> Int,
    onReplayClick: () -> Unit,
    onPlayToggle: () -> Unit,
    onForwardClick: () -> Unit
) {

    val playing = remember(isPlaying()) { isPlaying() }
    val playerState = remember(playbackState()) { playbackState() }

    //black overlay across the video player
    Row(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.CenterVertically
    ) {
        //Replay button
        CustomIconButton(
            modifier = Modifier.size(35.dp),
            imageId = R.drawable.ic_replay_5,
            color = Color.White,
            onClick = { onReplayClick() }
        )

        //Play button
        CustomIconButton(
            modifier = Modifier.size(35.dp),
            imageId = when {
                playerState == STATE_PAUSED -> {
                    R.drawable.ic_replay_circle
                }

                playing -> {
                    R.drawable.ic_pause_24
                }

                else -> {
                    R.drawable.ic_play_circle
                }
            },
            color = Color.White,
            onClick = { onPlayToggle() }
        )

        //Forward button
        CustomIconButton(
            modifier = Modifier.size(35.dp),
            imageId = R.drawable.ic_forward_10,
            color = Color.White,
            onClick = { onForwardClick() }
        )
    }
}