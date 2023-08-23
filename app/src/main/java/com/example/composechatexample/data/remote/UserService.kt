package com.example.composechatexample.data.remote

import com.example.composechatexample.data.model.UserFromId
import com.example.composechatexample.data.response.DefaultResponse
import com.example.composechatexample.domain.model.Friend
import com.example.composechatexample.domain.model.NewUserInfo
import com.example.composechatexample.utils.Constants

interface UserService {

    suspend fun getUserById(uid: String): UserFromId?

    suspend fun friendshipRequest(selfId: String, userId: String): Boolean?

    suspend fun getFollowerFriends(uid: String, type: String): List<Friend>?

    suspend fun friendshipAccept(selfId: String, userId: String, action: Boolean): Boolean

    suspend fun updateUserInfo(newInfo: NewUserInfo): Boolean

    suspend fun setAvatar(userId: String, img: ByteArray): DefaultResponse?

    sealed class EndPoint(val url: String) {
        object GetUserById : EndPoint("${Constants.BASE_URL}/get_user")
        object UpdateUser : EndPoint("${Constants.BASE_URL}/update_user")
        object AddFriends : EndPoint("${Constants.BASE_URL}/add_friend")
        object GetFollowerFriends : EndPoint("${Constants.BASE_URL}/get_follower_friends")
        object FriendshipAction : EndPoint("${Constants.BASE_URL}/friendship")
        object UploadAvatar : EndPoint("${Constants.BASE_URL}/upload_avatar")
    }
}