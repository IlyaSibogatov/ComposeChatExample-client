package com.example.composechatexample.screens.notifications

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavController
import com.example.composechatexample.R
import com.example.composechatexample.components.CircularLoader
import com.example.composechatexample.components.CoilImage
import com.example.composechatexample.components.CustomIconButton
import com.example.composechatexample.components.EmptyOrErrorView
import com.example.composechatexample.data.model.NotificationType
import com.example.composechatexample.data.model.UserNotification
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.ScreenState

@Composable
fun NotificationsScreen(
    navController: NavController
) {
    val context = LocalContext.current
    val viewModel: NotificationsViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    when (uiState.value.screenState) {
        ScreenState.INIT -> {
            CircularLoader()
        }

        ScreenState.SUCCESS -> {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                items(uiState.value.notifications) { item ->
                    NotificationItem(
                        item,
                        acceptClick = { viewModel.friendshipAction(item, true) },
                        declinedClick = { viewModel.friendshipAction(item) }
                    )
                }
            }
        }

        ScreenState.ERROR -> {
            EmptyOrErrorView(
                modifier = Modifier.fillMaxSize(),
                isError = true
            )
        }

        ScreenState.EMPTY_DATA -> {
            EmptyOrErrorView(
                modifier = Modifier.fillMaxSize(),
            )
        }

        else -> {}
    }
    val lifeCycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) viewModel.getNotifications()
            if (event == Lifecycle.Event.ON_RESUME) viewModel.notificationScreenOpen(true)
            if (event == Lifecycle.Event.ON_PAUSE) viewModel.notificationScreenOpen(false)
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun NotificationItem(
    item: UserNotification,
    acceptClick: () -> Unit,
    declinedClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .fillMaxWidth(),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            /** Avatar */
            Card(
                modifier = Modifier
                    .padding(8.dp)
                    .size(24.dp),
                shape = CircleShape,
            ) {
                val url = Constants.BASE_URL + "/images/" + item.senderId + ".jpeg"
                CoilImage(
                    data = url,
                    placeholder = R.drawable.ic_user
                )
            }
            /** Notification text */
            Text(
                modifier = Modifier
                    .weight(1f),
                text = when (item.type) {
                    NotificationType.REQUEST_FRIENDSHIP -> {
                        stringResource(id = R.string.request_friendship, item.senderName)
                    }

                    NotificationType.ACCEPTED_FRIENDSHIP -> {
                        stringResource(id = R.string.accepted_friendship, item.senderName)
                    }

                    NotificationType.DECLINED_FRIENDSHIP -> {
                        stringResource(id = R.string.declined_friendship, item.senderName)
                    }

                    NotificationType.USER_ACCEPT_FRIENDSHIP -> {
                        stringResource(id = R.string.user_accepted_friendship, item.senderName)
                    }

                    NotificationType.USER_DECLINED_FRIENDSHIP -> {
                        stringResource(id = R.string.user_declined_friendship, item.senderName)
                    }
                }
            )
            /** Friend request action buttons */
            if (item.type == NotificationType.REQUEST_FRIENDSHIP) {
                CustomIconButton(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(24.dp),
                    imageId = R.drawable.ic_accept,
                    color = Color(0xFF1C631F),
                    onClick = { acceptClick() },
                )
                CustomIconButton(
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .size(24.dp),
                    imageId = R.drawable.ic_declined,
                    color = Color(0xFFC81F1F),
                    onClick = { declinedClick() },
                )
            }
        }
    }
}