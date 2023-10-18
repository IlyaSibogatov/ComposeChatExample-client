package com.example.composechatexample.data.remote

import com.example.composechatexample.data.model.UserChatInfo
import com.example.composechatexample.domain.model.Chat
import com.example.composechatexample.domain.model.Message
import com.example.composechatexample.domain.model.NewChat
import com.example.composechatexample.utils.Constants.BASE_URL

interface MessageService {
    suspend fun getAllMessages(chatId: String, uid: String): List<Message>

    suspend fun getFollowers(chatId: String): List<UserChatInfo>

    suspend fun getAllChats(page: Int, limit: Int): List<Chat>?

    suspend fun createChat(chat: NewChat): Chat?

    suspend fun updateChat(chat: NewChat): Chat?

    suspend fun deleteChat(chatId: String): Boolean

    sealed class EndPoint(val url: String) {
        object GetAllMessages : EndPoint("$BASE_URL/messages")
        object GetFollowers : EndPoint("$BASE_URL/followers")
        object GetAllChats : EndPoint("$BASE_URL/chats")
        object CreateChat : EndPoint("$BASE_URL/create")
        object UpdateChat : EndPoint("$BASE_URL/update")
        object DeleteChat : EndPoint("$BASE_URL/delete")
    }
}