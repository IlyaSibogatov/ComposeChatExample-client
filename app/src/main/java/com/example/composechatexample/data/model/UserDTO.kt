package com.example.composechatexample.data.model

import com.example.composechatexample.domain.model.User
import kotlinx.serialization.Serializable

@Serializable
data class UserDTO(
    val username: String,
    val password: String,
    val selfInfo: String,
    var onlineStatus: Boolean,
    var lastActionTime: Long,
    val timestamp: Long,
    val id: String,
) {
    fun toUser(): User {
        return User(
            username = username,
            password = password,
            selfInfo = selfInfo,
            onlineStatus = onlineStatus,
            lastActionTime = lastActionTime,
            timestamp = timestamp,
            id = id
        )
    }
}
