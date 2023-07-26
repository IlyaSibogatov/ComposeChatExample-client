package com.example.composechatexample.data.remote

import com.example.composechatexample.data.model.MessageDTO
import com.example.composechatexample.domain.model.Message
import com.example.composechatexample.utils.Resources
import io.ktor.client.HttpClient
import io.ktor.client.features.websocket.webSocketSession
import io.ktor.client.request.url
import io.ktor.http.cio.websocket.Frame
import io.ktor.http.cio.websocket.WebSocketSession
import io.ktor.http.cio.websocket.close
import io.ktor.http.cio.websocket.readText
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.isActive
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

class ChatSocketServiceImpl(
    private val client: HttpClient,
) : ChatSocketService {

    private var socket: WebSocketSession? = null
    override suspend fun initSession(
        username: String,
        userId: String,
        chatId: String
    ): Resources<Unit> {
        return try {
            socket = client.webSocketSession {
                url("${ChatSocketService.EndPoint.ChatSocketRoute.url}?username=$username&userId=$userId&chatId=$chatId")
            }
            if (socket?.isActive == true) Resources.Success(Unit)
            else Resources.Error("Couldn't establish connection")
        } catch (e: Exception) {
            e.printStackTrace()
            Resources.Error(e.localizedMessage ?: "Unknown error")
        }
    }

    override suspend fun sendMessage(msg: String) {
        try {
            socket?.send(Frame.Text(msg))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun observeMessages(myName: String): Flow<Message> {
        return try {
            socket?.incoming
                ?.receiveAsFlow()
                ?.filter { it is Frame.Text }
                ?.map {
                    val json = (it as? Frame.Text)?.readText() ?: ""
                    val messageDTO = Json.decodeFromString<MessageDTO>(json)
                    messageDTO.toMessage(myName)
                } ?: flow { }
        } catch (e: Exception) {
            e.printStackTrace()
            flow { }
        }
    }

    override suspend fun closeSessions() {
        socket?.close()
    }
}