package com.example.composechatexample.data.model

import com.example.composechatexample.domain.model.Chat
import kotlinx.serialization.Serializable
import java.util.Date

@Serializable
data class ChatDTO(
    val id: String,
    val timestamp: Long,
    val name: String,
    val password: String = "",
    val owner: String,
    val ownerId: String,
) {
    fun toChat(): Chat {
        val date = Date(timestamp)
        val fd = android.text.format.DateFormat.format(
            "dd-MM-yyyy HH:MM", date
        ).toString()
        return Chat(
            name = name,
            password = password,
            owner = owner,
            formattedTime = fd,
            id = id,
            ownerId = ownerId,
        )
    }
}
