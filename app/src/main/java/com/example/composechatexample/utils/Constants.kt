package com.example.composechatexample.utils

import com.example.composechatexample.R
import com.example.composechatexample.data.model.LanguageEntity

object Constants {
    const val ONBOARD_ROUTE = "onboard_screen"
    const val ONBOARD_TITLE = "Onboard"

    const val PROFILE_ROUTE = "profile_screen"
    const val PROFILE_TITLE = "Profile"

    const val NOTIFICATIONS_ROUTE = "notifications_screen"
    const val NOTIFICATION_TITLE = "Notification"

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

    const val CONTENT_DESCRIPTION = "Content description"
    const val CHAT_ID = "chat_id"

    const val USER_UID = "user_uid"
    const val USERS_TYPE = "users_type"

    const val FOLLOWERS = "followers"
    const val FRIENDSHIPS_REQUESTS = "friendship_requests"
    const val FRIENDS = "friends"

    const val POP_BACK_STACK = "popBackStack"

    const val SUCCESS_TAG = "\uD83D\uDFE2\n"
    const val FAILED_TAG = "\uD83D\uDD34\n"

    const val ELEMENT_LIMIT = 6
    const val ENDLESS_LIST_INIT_PAGE = 1

    val listLanguages = listOf(
        LanguageEntity("Русский", "ru"),
        LanguageEntity("English", "en"),
    )
    val listTypeTheme = listOf(
        Type(nameType = TypeTheme.SYSTEM, str = R.string.theme_text_system),
        Type(nameType = TypeTheme.DARK, str = R.string.theme_text_dark),
        Type(nameType = TypeTheme.LIGHT, str = R.string.theme_text_light),
    )
    val listTypeLang = listOf(
        Type(nameType = TypeLang.RU, str = R.string.languages_text_ru),
        Type(nameType = TypeLang.ENG, str = R.string.languages_text_eng)
    )
    val listTypeNotifications = listOf(
        Type(nameType = TypeNotification.ENABLE, str = R.string.enabled_label),
        Type(nameType = TypeNotification.DISABLE, str = R.string.disabled_label)
    )

    val profileMenu = listOf(
        Type(nameType = TypeMenuItem.SHARE , str = R.string.share_label),
        Type(nameType = TypeMenuItem.EDIT , str = R.string.edit_profile)
    )

    // Use 10.0.2.2:8080 if emulator is used
    const val BASE_URL = "http://10.0.2.2:8080"
    const val BASE_SOCKET_URL = "ws://10.0.2.2:8080"
}