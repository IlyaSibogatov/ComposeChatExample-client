package com.example.composechatexample.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Chat(
    val name: String,
    val password: String,
    val owner: String,
    val formattedTime: String,
    val id: String,
)

@Serializable
data class NewChat(
    val id: String,
    val name: String,
    val password: String,
    val owner: String,
)

enum class ChatEvent {
    EDIT, REMOVE
}