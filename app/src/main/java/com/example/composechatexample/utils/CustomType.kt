package com.example.composechatexample.utils

import androidx.annotation.StringRes

data class Type<T>(
    val nameType: T,
    @StringRes val str: Int
)

enum class TypeTheme {
    DARK, LIGHT, SYSTEM
}

enum class SendType {
    REMOVE, SEND, EDIT
}

enum class ChatEvent {
    EDIT, REMOVE
}

