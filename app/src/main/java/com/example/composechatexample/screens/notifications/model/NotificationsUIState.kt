package com.example.composechatexample.screens.notifications.model

import com.example.composechatexample.data.model.UserNotification
import com.example.composechatexample.utils.ScreenState

data class NotificationsUIState(
    val notifications: List<UserNotification> = listOf(),
    val screenState: ScreenState? = null
)