package com.example.composechatexample.utils.player

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.composechatexample.R
import com.example.composechatexample.components.CustomIconButton
import com.example.composechatexample.ui.theme.DarkBackground

@Composable
fun TopControls(
    modifier: Modifier = Modifier,
    title: () -> String,
    onBackClick: () -> Unit
) {
    val videoTitle = remember(title()) { title() }
    Row(
        modifier = modifier.background(color = DarkBackground.copy(alpha = 0.7f)),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CustomIconButton(
            imageId = R.drawable.ic_arrow_back,
            onClick = { onBackClick() },
            color = Color.White
        )
        Text(
            text = videoTitle,
            color = Color.White,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}