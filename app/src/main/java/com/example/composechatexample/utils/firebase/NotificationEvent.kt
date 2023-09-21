package com.example.composechatexample.utils.firebase

import com.example.composechatexample.data.model.UserNotification

interface NotificationEvent {
    data class AddNotification(val notification: UserNotification) : NotificationEvent
}