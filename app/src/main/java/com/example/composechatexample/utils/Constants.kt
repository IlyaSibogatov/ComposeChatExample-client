package com.example.composechatexample.utils

import com.example.composechatexample.data.model.LanguageEntity

object Constants {
    const val ONBOARD_ROUTE = "onboard_screen"
    const val ONBOARD_TITLE = "Onboard"

    const val PROFILE_ROUTE = "profile_screen"
    const val PROFILE_TITLE = "Profile"

    const val CHAT_ROUTE = "chat_screen"
    const val CHAT_TITLE = "Chat"

    const val CHAT_LIST_ROUTE = "chat_list_route"
    const val CHAT_LIST_TITLE = "Chat list"

    const val SETTINGS_ROUTE = "setting_screen"
    const val SETTING_TITLE = "Settings"

    const val CREATE_CHAT_ROUTE = "create_chat_screen"
    const val CREATE_CHAT_TITLE = "Create chat"

    const val FRIENDS_LIST_ROUTE = "friends_list_route"
    const val FRIENDS_LIST_TITLE = "Friends list"

    const val LANGUAGE_ROUTE = "languages_route"
    const val LANGUAGE_TITLE = "Languages list"

    const val CONTENT_DESCRIPTION = "Content description"
    const val CHAT_ID = "chat_id"

    const val USER_UID = "user_uid"

    val listLanguages = listOf<LanguageEntity>(
        LanguageEntity("Русский", "ru"),
        LanguageEntity("English", "en"),
    )

    // Use 10.0.2.2:8080 if emulator is used
    const val BASE_URL = "http://10.0.2.2:8080"
    const val BASE_SOCKET_URL = "ws://10.0.2.2:8080"
}