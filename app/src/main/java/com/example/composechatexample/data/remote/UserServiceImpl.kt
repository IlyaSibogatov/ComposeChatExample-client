package com.example.composechatexample.data.remote

import com.example.composechatexample.data.model.UserFromId
import com.example.composechatexample.data.model.UserNotification
import com.example.composechatexample.data.response.DefaultResponse
import com.example.composechatexample.domain.model.Friend
import com.example.composechatexample.domain.model.NewUserInfo
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType

class UserServiceImpl(
    private val client: HttpClient
) : UserService {
    override suspend fun getUserById(uid: String): UserFromId? {
        return try {
            client.get<UserFromId>(UserService.EndPoint.GetUserById.url) {
                url.parameters.append("uid", uid)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun friendshipRequest(selfId: String, userId: String): DefaultResponse? {
        return try {
            client.post<DefaultResponse>(UserService.EndPoint.AddFriends.url) {
                url.parameters.append("selfId", selfId)
                url.parameters.append("userId", userId)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getFollowerFriends(uid: String, type: String): List<Friend>? {
        return try {
            client.get<List<Friend>>(UserService.EndPoint.GetFollowerFriends.url) {
                url.parameters.append("uid", uid)
                url.parameters.append("type", type)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun updateUserInfo(newInfo: NewUserInfo): Boolean {
        return try {
            client.post<DefaultResponse>(UserService.EndPoint.UpdateUser.url) {
                body = newInfo
                contentType(ContentType.Application.Json)
            }.let {
                it.status == HttpStatusCode.OK.value
            }
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    override suspend fun friendshipAccept(
        selfId: String,
        userId: String,
        action: Boolean
    ): DefaultResponse? {
        return try {
            client.post<DefaultResponse>(UserService.EndPoint.FriendshipAction.url) {
                url.parameters.append("selfId", selfId)
                url.parameters.append("userId", userId)
                url.parameters.append("accept", action.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun removeFriend(
        selfId: String,
        userId: String,
        selfRemoving: Boolean
    ): DefaultResponse? {
        return try {
            client.post<DefaultResponse>(UserService.EndPoint.RemoveFriend.url) {
                url.parameters.append("selfId", selfId)
                url.parameters.append("userId", userId)
                url.parameters.append("selfRemoving", selfRemoving.toString())
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun updateToken(
        uuid: String,
        token: String,
        deviceId: String,
        deviceType: String
    ): DefaultResponse? {
        return try {
            client.post<DefaultResponse>(UserService.EndPoint.UpdateToken.url) {
                url.parameters.append("uuid", uuid)
                url.parameters.append("token", token)
                url.parameters.append("deviceId", deviceId)
                url.parameters.append("deviceType", deviceType)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun getNotifications(uuid: String): List<UserNotification>? {
        return try {
            client.get<List<UserNotification>>(UserService.EndPoint.GetNotifications.url) {
                url.parameters.append("uuid", uuid)
            }
        } catch (e: Exception) {
            null
        }
    }
}