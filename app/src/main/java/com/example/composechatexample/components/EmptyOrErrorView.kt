package com.example.composechatexample.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import com.example.composechatexample.R
import com.example.composechatexample.utils.Constants

@Composable
fun EmptyOrErrorView(
    modifier: Modifier = Modifier.fillMaxSize(),
    screen: String? = null,
    isError: Boolean = false
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            modifier = Modifier
                .fillMaxWidth(),
            painter = painterResource(id = if (isError) R.drawable.ic_connect_error else R.drawable.ic_alert),
            contentDescription = Constants.CONTENT_DESCRIPTION,
        )
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(
                id = if (isError) R.string.try_again_later
                else
                    R.string.empty_data,
                if (screen == Constants.CHAT_TITLE) stringResource(id = R.string.chats)
                else stringResource(id = R.string.notifications)
            ),
            textAlign = TextAlign.Center
        )
    }
}