package com.example.composechatexample.screens.notifications.model

import com.example.composechatexample.data.model.UserNotification

data class NotificationsUIState(
    val notifications: List<UserNotification> = listOf()
)