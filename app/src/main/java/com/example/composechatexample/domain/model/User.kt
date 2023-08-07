package com.example.composechatexample.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class User(
    val username: String,
    val password: String,
    val selfInfo: String,
    var onlineStatus: Boolean,
    var lastActionTime: Long,
    val timestamp: Long,
    val friendsList: List<Friend> = listOf(),
    val id: String,
)

@Serializable
data class NewUserInfo(
    var id: String = "",
    val username: String = "",
    val selfInfo: String = "",
)

@Serializable
data class Friend(
    val id: String = "",
    val username: String = "",
    var onlineStatus: Boolean = false,
)

@Serializable
data class UserCredentials(
    val username: String,
    val password: String,
)