package com.example.composechatexample.data.model

import kotlinx.serialization.Serializable

@Serializable
data class VideoItem(
    val id: String,
    var name: String,
    var description: String,
)

@Serializable
data class PhotoItem(
    val id: String,
    val description: String,
)

data class PhotoSource(
    var description: String = "",
    var image: ByteArray,
)

data class VideoSource(
    val name: String,
    var description: String,
    var video: ByteArray,
    var image: ByteArray
)