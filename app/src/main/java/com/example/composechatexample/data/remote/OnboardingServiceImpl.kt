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

    override suspend fun logout(uid: String): DefaultResponse? {
        return try {
            client.post<DefaultResponse>(OnboardingService.EndPoint.Logout.url) {
                url.parameters.append("uid", uid)
            }
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun changePass(current: String, new: String, uuid: String): String? {
        return try {
            client.post<String>(OnboardingService.EndPoint.ChangePass.url) {
                url.parameters.append("uuid", uuid)
                url.parameters.append("current", current)
                url.parameters.append("new", new)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun deleteAccount(uuid: String): DefaultResponse? {
        return try {
            client.post<DefaultResponse>(OnboardingService.EndPoint.DeleteAcc.url){
                url.parameters.append("uuid", uuid)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}