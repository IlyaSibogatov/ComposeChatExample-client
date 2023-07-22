package com.example.composechatexample.domain.model

data class Friend(
    val id: String = "",
    val isOnline: Boolean = false,
    val username: String = "",
    val photo: String = "",
)
