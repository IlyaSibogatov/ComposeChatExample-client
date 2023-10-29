package com.example.composechatexample.data.model

import com.example.composechatexample.domain.model.Friend
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
    val avatarId: String,
    val username: String,
    val avatar: String? = null,
    val selfInfo: String,
    var onlineStatus: Boolean,
    var lastActionTime: Long,
    val friends: List<Friend>,
    val followers: List<String>,
    val listPhotos: List<PhotoItem>,
    val listVideos: List<VideoItem>,
    val friendshipRequests: List<FriendShipRequest>,
)

@Serializable
data class UserChatInfo(
    val uuid: String,
    val username: String,
)

@Serializable
data class FriendShipRequest(
    val uuid: String,
    val id: String,
)