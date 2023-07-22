package com.example.composechatexample.screens.chat.chatdetails.model

sealed interface ChatScreenEvent {
    data class NavigateTo(val route: String) : ChatScreenEvent
    data class ToastEvent(val msg: String): ChatScreenEvent
}