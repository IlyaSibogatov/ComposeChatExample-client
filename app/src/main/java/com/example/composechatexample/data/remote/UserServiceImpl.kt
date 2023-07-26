package com.example.composechatexample.data.remote

import com.example.composechatexample.data.model.UserDTO
import com.example.composechatexample.domain.model.User
import io.ktor.client.HttpClient
import io.ktor.client.request.get

class UserServiceImpl(
    private val client: HttpClient
) : UserService {
    override suspend fun getUserById(uid: String): User? {
        return try {
            client.get<UserDTO>(UserService.EndPont.GetUserById.url) {
                url.parameters.append("uid", uid)
            }.toUser()
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}