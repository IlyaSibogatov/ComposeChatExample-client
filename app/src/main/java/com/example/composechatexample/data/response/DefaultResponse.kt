package com.example.composechatexample.data.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DefaultResponse(
    @SerialName ("msg") val msg: String,
    @SerialName ("status") val status: Int,
)
