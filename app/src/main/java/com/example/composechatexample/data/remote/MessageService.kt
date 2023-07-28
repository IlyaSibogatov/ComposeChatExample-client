package com.example.composechatexample.data.remote

import com.example.composechatexample.data.response.DefaultResponse
import com.example.composechatexample.domain.model.Chat
import com.example.composechatexample.domain.model.Message
import com.example.composechatexample.domain.model.NewChat
import com.example.composechatexample.utils.Constants.BASE_URL

interface MessageService {
    suspend fun getAllMessages(chatId: String, myName: String): List<Message>

    suspend fun getAllChats(): List<Chat>

    suspend fun createChat(chat: NewChat): DefaultResponse?

    suspend fun updateChat(chat: NewChat): DefaultResponse?

    suspend fun deleteChat(chatId: String): Boolean

    sealed class EndPoint(val url: String) {
        object GetAllMessages : EndPoint("$BASE_URL/messages")
        object GetAllChats : EndPoint("$BASE_URL/chats")
        object CreateChat : EndPoint("$BASE_URL/create")
        object UpdateChat : EndPoint("$BASE_URL/update")
        object DeleteChat : EndPoint("$BASE_URL/delete")
    }
}