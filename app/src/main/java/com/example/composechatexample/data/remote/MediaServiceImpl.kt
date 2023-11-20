package com.example.composechatexample.data.remote

import com.example.composechatexample.data.model.MediaDescription
import com.example.composechatexample.data.model.PhotoSource
import com.example.composechatexample.data.model.VideoSource
import com.example.composechatexample.data.response.DefaultResponse
import io.ktor.client.HttpClient
import io.ktor.client.request.forms.MultiPartFormDataContent
import io.ktor.client.request.forms.formData
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders

class MediaServiceImpl(
    private val client: HttpClient
) : MediaService {
    override suspend fun getMediaDescription(
        uuid: String,
        mediaId: String,
        type: String
    ): MediaDescription? {
        return try {
            client.get<MediaDescription>(MediaService.EndPoint.GetMediaDescription.url) {
                url.parameters.append("userId", uuid)
                url.parameters.append("mediaId", mediaId)
                url.parameters.append("type", type)
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    override suspend fun sendPhoto(
        userId: String,
        source: PhotoSource,
        isAvatar: Boolean,
    ): DefaultResponse? {
        return try {
            client.post(MediaService.EndPoint.SendPhoto.url) {
                url.parameters.append("userId", userId)
                url.parameters.append("isAvatar", isAvatar.toString())
                url.parameters.append("description", source.description)
                body = MultiPartFormDataContent(
                    formData {
                        append("description", "Ktor avatar")
                        append("image", source.image, Headers.build {
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

    override suspend fun sendVideo(
        userId: String,
        source: VideoSource
    ): DefaultResponse? {
        return try {
            client.post<DefaultResponse>(MediaService.EndPoint.SendVideo.url) {
                url.parameters.append("userId", userId)
                url.parameters.append("name", source.name)
                url.parameters.append("description", source.description)
                body = MultiPartFormDataContent(
                    formData {
                        append("description", "Ktor video")
                        append("video", source.video, Headers.build {
                            append(HttpHeaders.ContentType, "video/mp4")
                            append(HttpHeaders.ContentDisposition, "filename=video.mp4")
                        }
                        )
                        append("thumbnail", source.image, Headers.build {
                            append(HttpHeaders.ContentType, "image/jpeg")
                            append(HttpHeaders.ContentDisposition, "filename=image.png")
                        })
                    }
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}