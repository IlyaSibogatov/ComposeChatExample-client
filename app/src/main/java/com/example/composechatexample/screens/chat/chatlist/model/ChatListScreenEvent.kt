package com.example.composechatexample.screens.chat.chatlist.model

sealed interface ChatListScreenEvent {
    data class NavigateTo(val route: String) : ChatListScreenEvent
    data class ToastEvent(var msg: String): ChatListScreenEvent
}