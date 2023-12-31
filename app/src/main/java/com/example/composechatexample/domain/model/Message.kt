package com.example.composechatexample.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Message(
    val id: String,
    var message: String,
    val userId: String,
    var myMessage: Boolean = false,
    var wasEdit: Boolean = false,
    val formattedTime: String,
)