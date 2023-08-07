package com.example.composechatexample.data.remote

import com.example.composechatexample.data.model.UserFromId
import com.example.composechatexample.domain.model.Friend
import com.example.composechatexample.domain.model.NewUserInfo
import com.example.composechatexample.utils.Constants

interface UserService {

    suspend fun getUserById(uid: String): UserFromId?

    suspend fun friendshipRequest(selfId: String, userId: String): Boolean?

    suspend fun getFollowerFriends(uid: String, type: String): List<Friend>?

    suspend fun friendshipAccept(selfId: String, userId: String, action: Boolean): Boolean
    suspend fun updateUserInfo(newInfo: NewUserInfo): Boolean

    sealed class EndPoint(val url: String) {
        object GetUserById : EndPoint("${Constants.BASE_URL}/getUser")
        object UpdateUser : EndPoint("${Constants.BASE_URL}/updateuser")
        object AddFriends : EndPoint("${Constants.BASE_URL}/addFriend")

        object GetFollowerFriends : EndPoint("${Constants.BASE_URL}/getFollowerFriends")

        object FriendshipAction : EndPoint("${Constants.BASE_URL}/friendship")
    }
}