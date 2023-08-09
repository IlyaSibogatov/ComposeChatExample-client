package com.example.composechatexample.data.model

import com.example.composechatexample.domain.model.Message
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class MessageDTO(
    val id: String,
    val timestamp: Long,
    val message: String,
    val userId: String,
    val wasEdit: Boolean,
) {
    fun toMessage(uid: String): Message {
        val date = Date(timestamp)
        val fd = android.text.format.DateFormat.format(
            "dd-MM-yyyy HH:MM:ss", date
        ).toString()
        return Message(
            id = id,
            message = message,
            formattedTime = fd,
            userId = userId,
            wasEdit = wasEdit,
            myMessage = userId == uid
        )
    }
}