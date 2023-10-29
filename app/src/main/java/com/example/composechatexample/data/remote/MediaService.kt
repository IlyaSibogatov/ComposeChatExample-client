package com.example.composechatexample.data.remote

import com.example.composechatexample.data.model.PhotoSource
import com.example.composechatexample.data.model.VideoSource
import com.example.composechatexample.data.response.DefaultResponse
import com.example.composechatexample.utils.Constants

interface MediaService {

    suspend fun sendPhoto(
        userId: String,
        source: PhotoSource,
        isAvatar: Boolean = false
    ): DefaultResponse?

    suspend fun sendVideo(
        userId: String,
        source: VideoSource
    ): DefaultResponse?

    sealed class EndPoint(val url: String) {
        object SendPhoto : UserService.EndPoint("${Constants.BASE_URL}/upload_photo")
        object SendVideo : UserService.EndPoint("${Constants.BASE_URL}/upload_video")
    }
}