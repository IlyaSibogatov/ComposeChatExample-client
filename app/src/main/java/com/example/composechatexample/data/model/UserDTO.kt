package com.example.composechatexample.data.model

import com.example.composechatexample.domain.model.Friend
import com.example.composechatexample.domain.model.User
import kotlinx.serialization.Serializable
import java.io.File

@Serializable
data class UserDTO(
    val username: String,
    val password: String,
    val selfInfo: String,
    var onlineStatus: Boolean,
    var lastActionTime: Long,
    val timestamp: Long,
    val friendsList: ArrayList<Friend> = ArrayList(),
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

@Serializable
data class UserFromId(
    val id: String,
    val username: String,
    val avatar: String? = null,
    val selfInfo: String,
    var onlineStatus: Boolean,
    var lastActionTime: Long,
    val friends: List<Friend>,
    val followers: List<String>,
    val friendshipRequests: List<String>,
)

@Serializable
data class UserChatInfo(
    val uuid: String,
    val username: String,
)