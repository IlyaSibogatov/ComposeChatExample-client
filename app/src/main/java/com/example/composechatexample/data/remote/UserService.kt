package com.example.composechatexample.data.remote

import com.example.composechatexample.data.model.UserFromId
import com.example.composechatexample.data.model.UserNotification
import com.example.composechatexample.data.response.DefaultResponse
import com.example.composechatexample.domain.model.Friend
import com.example.composechatexample.domain.model.NewUserInfo
import com.example.composechatexample.utils.Constants

interface UserService {

    suspend fun getUserById(uid: String): UserFromId?

    suspend fun friendshipRequest(selfId: String, userId: String): DefaultResponse?

    suspend fun getFollowerFriends(uid: String, type: String): List<Friend>?

    suspend fun friendshipAccept(selfId: String, userId: String, action: Boolean): DefaultResponse?

    suspend fun updateToken(
        uuid: String,
        token: String,
        deviceId: String,
        type: String
    ): DefaultResponse?

    suspend fun getNotifications(uuid: String): List<UserNotification>?

    suspend fun removeFriend(
        selfId: String,
        userId: String,
        selfRemoving: Boolean
    ): DefaultResponse?

    suspend fun updateUserInfo(newInfo: NewUserInfo): Boolean

    sealed class EndPoint(val url: String) {
        object GetUserById : EndPoint("${Constants.BASE_URL}/get_user")
        object UpdateUser : EndPoint("${Constants.BASE_URL}/update_user")
        object AddFriends : EndPoint("${Constants.BASE_URL}/add_friend")
        object GetFollowerFriends : EndPoint("${Constants.BASE_URL}/get_follower_friends")
        object FriendshipAction : EndPoint("${Constants.BASE_URL}/friendship")

        object RemoveFriend : EndPoint("${Constants.BASE_URL}/remove_friend")

        object UpdateToken : EndPoint("${Constants.BASE_URL}/update_token")

        object GetNotifications : EndPoint("${Constants.BASE_URL}/notifications")
    }
}