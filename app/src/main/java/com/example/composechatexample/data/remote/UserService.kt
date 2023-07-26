package com.example.composechatexample.data.remote

import com.example.composechatexample.domain.model.User
import com.example.composechatexample.utils.Constants

interface UserService {

    suspend fun getUserById(uid: String): User?

    sealed class EndPont(val url: String) {
        object GetUserById : EndPont("${Constants.BASE_URL}/getUser")
    }
}