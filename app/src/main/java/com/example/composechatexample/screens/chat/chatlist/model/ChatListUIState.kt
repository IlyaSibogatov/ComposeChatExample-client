package com.example.composechatexample.screens.chat.chatlist.model

import com.example.composechatexample.domain.model.Chat

data class ChatListUIState(
    val isLoading: Boolean = false,
    val userLogged: Boolean = false,
    val roomPassword: String = "",
    val searchQuery: String = "",
    var username: String = "",
    val chats: MutableList<Chat> = mutableListOf(),
    val newChats: List<Chat> = listOf(),
    var chatInfo: Chat? = null,
    val dialogs: DisplayDialog = DisplayDialog(),
    var errors: ChatListErrors = ChatListErrors(),
    val createdChat: CreatedChat = CreatedChat()
)

data class DisplayDialog(
    val passDialog: Boolean = false,
    val createDialog: Boolean = false,
)

data class ChatListErrors(
    val entryPasswordError: Boolean = false,
    val emptyChatName: Boolean = false,
    val emptyChatPass: Boolean = false,
)

data class CreatedChat(
    val chatName: String = "",
    val chatPass: String = "",
    val passEnable: Boolean = false,
)