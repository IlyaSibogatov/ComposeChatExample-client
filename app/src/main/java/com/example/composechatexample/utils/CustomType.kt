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

enum class ViewForDisplay {
    ADD_FRIEND,
    REMOVE_FRIEND,
    PROGRESS_LINEAR,
    EDIT_INFO,
}

enum class ResponseStatus(val value: String) {
    SUCCESS("success"),
    FAILED("failed"),
    ERROR("error"),

    INFO_UPDATED("info_was_update"),
    INFO_NOT_UPDATED("info_not_update"),
    FRIENDSHIP_REQUEST_SEND("request_was_send"),
    FRIENDSHIP_REQUEST_NOT_SEND("request_not_send"),

    FRIEND_REMOVED("friend removed")
}

enum class PassUpdateState(val value: String) {
    EMPTY_FIELD("empty_field"),
    NOT_MATCH_PATTERN("not_match_pattern"),
    NEW_CURRENT_SAME("new_current_same"),
    NEWEST_NOT_SAME("newest_pass_not_same"),
    NEW_EQ_OLD("new_equals_old"),
    CURRENT_NOT_EQ_OLD("current_not_eq_old"),
    FIELD_CORRECTLY("field_correctly"),
}