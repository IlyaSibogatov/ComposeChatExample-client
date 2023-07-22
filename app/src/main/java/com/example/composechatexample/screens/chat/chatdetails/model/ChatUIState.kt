package com.example.composechatexample.screens.chat.chatdetails.model

import com.example.composechatexample.domain.model.Message

data class ChatUIState(
    val isLoading: Boolean = false,
    val onSending: Boolean = false,
    val editSelect: Boolean = false,
    val messages: List<Message> = listOf(),
    val selectedMsgId: String = "",
    val message: String = "",
    val username: String = "",
    val chatId: String = "",
)
