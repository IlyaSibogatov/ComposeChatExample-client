package com.example.composechatexample.data.remote

import com.example.composechatexample.data.response.DefaultResponse
import com.example.composechatexample.domain.model.UserCredentials
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.http.ContentType
import io.ktor.http.contentType

class OnboardingServiceImpl(
    private val client: HttpClient,
) : OnboardingService {
    override suspend fun login(userCredentials: UserCredentials): DefaultResponse? {
        return try {
            client.post<DefaultResponse>(OnboardingService.EndPoint.Login.url) {
                body = userCredentials
                contentType(ContentType.Application.Json)
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun signup(userCredentials: UserCredentials): DefaultResponse? {
        return try {
            client.post<DefaultResponse>(OnboardingService.EndPoint.SignUp.url) {
                body = userCredentials
                contentType(ContentType.Application.Json)
            }
        } catch (e: Exception) {
            null
        }
    }
}