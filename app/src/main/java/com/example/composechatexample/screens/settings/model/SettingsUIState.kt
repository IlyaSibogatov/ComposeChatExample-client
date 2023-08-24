package com.example.composechatexample.screens.settings.model

data class SettingsUIState(
    val language: String? = null,
    val theme: String? = null,
    val privacy: String = "",
    val notification: String = "",
    val blackList: List<String> = listOf("user1", "user2", "user3", "user4"),
    val support: String = "",
)