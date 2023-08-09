package com.example.composechatexample.screens.chat.chatdetails.model

import com.example.composechatexample.data.model.UserChatInfo
import com.example.composechatexample.domain.model.Message

data class ChatUIState(
    val isLoading: Boolean = false,
    val onSending: Boolean = false,
    val editSelect: Boolean = false,
    val messages: List<Message> = listOf(),
    val usersInfo: List<UserChatInfo> = listOf(),
    val selectedMsgId: String = "",
    val message: String = "",
    val username: String = "",
    val userId: String = "",
    val chatId: String = "",
)
