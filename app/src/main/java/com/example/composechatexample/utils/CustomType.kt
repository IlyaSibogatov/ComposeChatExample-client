package com.example.composechatexample.utils

import androidx.annotation.StringRes

data class Type<T>(
    val nameType: T,
    @StringRes val str: Int
)

enum class SettingType {
    LANG, THEME, NOTIFICATION, PERS_DATA, CONFIDENTIALITY, EDIT_PASSWORD
}

enum class TypeLang {
    RU, ENG
}

enum class TypeTheme {
    DARK, LIGHT, SYSTEM
}

enum class SendType {
    REMOVE, SEND, EDIT
}

enum class ChatEvent {
    EDIT, REMOVE
}

enum class ResponseStatus(val value: String) {
    SUCCESS("success"),
    FAILED("failed"),
    ERROR("error"),

    INFO_UPDATED("info_was_update"),
    INFO_NOT_UPDATED("info_not_update"),
    FRIENDSHIP_REQUEST_SEND("request_was_send"),
    FRIENDSHIP_REQUEST_NOT_SEND("request_not_send"),
}