package com.example.composechatexample.screens.chat.chatlist.model

import com.example.composechatexample.screens.chat.chatdetails.model.ChatScreenEvent

sealed interface ChatListScreenEvent {
    data class NavigateTo(val route: String) : ChatListScreenEvent
    data class ToastEvent(val msg: String): ChatListScreenEvent
}