package com.example.composechatexample.data.remote

import com.example.composechatexample.data.model.ChatDTO
import com.example.composechatexample.data.model.MessageDTO
import com.example.composechatexample.data.model.UserChatInfo
import com.example.composechatexample.data.response.DefaultResponse
import com.example.composechatexample.domain.model.Chat
import com.example.composechatexample.domain.model.Message
import com.example.composechatexample.domain.model.NewChat
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType

class MessageServiceImpl(
    private val client: HttpClient,
) : MessageService {
    override suspend fun getAllMessages(chatId: String, uid: String): List<Message> {
        return try {
            client.get<List<MessageDTO>>(MessageService.EndPoint.GetAllMessages.url) {
                url.parameters.append("chatId", chatId)
            }
                .map { it.toMessage(uid) }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getFollowers(chatId: String): List<UserChatInfo> {
        return try {
            client.get<List<UserChatInfo>>(MessageService.EndPoint.GetFollowers.url) {
                url.parameters.append("chatId", chatId)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun getAllChats(): List<Chat> {
        return try {
            client.get<List<ChatDTO>>(MessageService.EndPoint.GetAllChats.url)
                .map { it.toChat() }
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    override suspend fun createChat(chat: NewChat): DefaultResponse? {
        return try {
            client.post<DefaultResponse>(MessageService.EndPoint.CreateChat.url) {
                body = chat
                contentType(ContentType.Application.Json)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun updateChat(chat: NewChat): DefaultResponse? {
        return try {
            client.post<DefaultResponse>(MessageService.EndPoint.UpdateChat.url) {
                body = chat
                contentType(ContentType.Application.Json)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun deleteChat(chatId: String): Boolean {
        return try {
            client.post<Boolean>(MessageService.EndPoint.DeleteChat.url) {
                url.parameters.append("chatId", chatId)
            }
        } catch (e: java.lang.Exception) {
            e.printStackTrace()
            false
        }
    }
}