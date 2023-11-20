package com.example.composechatexample.utils.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.composechatexample.R
import com.example.composechatexample.components.CustomIconButton
import com.example.composechatexample.ui.theme.DarkBackground
import com.example.composechatexample.ui.theme.Primary
import com.example.composechatexample.utils.Ext.formatMinSec

@Composable
fun BottomControls(
    modifier: Modifier = Modifier,
    isFullScreen: () -> Boolean,
    totalDuration: () -> Long,
    currentTime: () -> Long,
    bufferPercentage: () -> Int,
    openFullScreen: () -> Unit,
    onSeekChanged: (timeMs: Float) -> Unit
) {

    val duration = remember(totalDuration()) { totalDuration() }

    val videoTime = remember(currentTime()) { currentTime() }

    val buffer = remember(bufferPercentage()) { bufferPercentage() }

    val fullScreen = remember(isFullScreen()) { isFullScreen() }

    Column(
        modifier = modifier.background(color = DarkBackground.copy(alpha = 0.7f)),
    ) {
        Box(modifier = modifier.fillMaxWidth()) {
            //Buffer bar
            Slider(
                value = buffer.toFloat(),
                enabled = false,
                onValueChange = { },
                valueRange = 0f..100f,
                colors = SliderDefaults.colors(
                    disabledThumbColor = Color.Transparent,
                    disabledActiveTrackColor = Color.Gray,
                    disabledInactiveTrackColor = Color.LightGray
                )
            )

            //Seek bar
            Slider(
                modifier = Modifier.fillMaxWidth(),
                value = videoTime.toFloat(),
                onValueChange = onSeekChanged,
                valueRange = 0f..duration.toFloat(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.White,
                    activeTrackColor = Primary,
                    inactiveTrackColor = Color.Transparent
                )
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            //Total time
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = videoTime.formatMinSec() + " / " + duration.formatMinSec(),
                color = Color.White
            )

            //Fullscreen button
            CustomIconButton(
                imageId = if (fullScreen) R.drawable.ic_exit_fullscreen else R.drawable.ic_open_fullscreen,
                color = Color.White,
                onClick = { openFullScreen() }
            )
        }
    }
}