package com.example.composechatexample.data.remote

import com.example.composechatexample.domain.model.Message
import com.example.composechatexample.utils.Constants.BASE_SOCKET_URL
import com.example.composechatexample.utils.Resources
import kotlinx.coroutines.flow.Flow

interface ChatSocketService {
    suspend fun initSession(
        username: String,
        userId: String,
        chatId: String,
    ): Resources<Unit>

    suspend fun sendMessage(msg: String)

    fun observeMessages(myName: String): Flow<Message>

    suspend fun closeSessions()

    sealed class EndPoint(val url: String) {
        object ChatSocketRoute : EndPoint("$BASE_SOCKET_URL/chat-socket")
    }
}