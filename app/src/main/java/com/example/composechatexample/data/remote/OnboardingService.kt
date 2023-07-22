package com.example.composechatexample.data.remote

import com.example.composechatexample.data.response.DefaultResponse
import com.example.composechatexample.domain.model.UserCredentials
import com.example.composechatexample.utils.Constants

interface OnboardingService {
    suspend fun login(userCredentials: UserCredentials): DefaultResponse?

    suspend fun signup(userCredentials: UserCredentials):DefaultResponse?

    sealed class EndPoint(val url: String) {
        object Login : EndPoint("${Constants.BASE_URL}/login")
        object SignUp : EndPoint("${Constants.BASE_URL}/signup")
    }
}