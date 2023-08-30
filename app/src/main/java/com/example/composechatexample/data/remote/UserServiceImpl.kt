package com.example.composechatexample.data.remote

import com.example.composechatexample.data.model.UserFromId
import com.example.composechatexample.data.response.DefaultResponse
import com.example.composechatexample.domain.model.Friend
import com.example.composechatexample.domain.model.NewUserInfo
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
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

    override suspend fun setAvatar(userId: String, img: ByteArray): DefaultResponse? {
        return try {
            client.post(UserService.EndPoint.UploadAvatar.url) {
                url.parameters.append("userId", userId)
                body = MultiPartFormDataContent(
                    formData {
                        append("description", "Ktor avatar")
                        append("image", img, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=image.png")
                        }
                        )
                    }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
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
}