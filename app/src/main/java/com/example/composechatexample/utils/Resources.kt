package com.example.composechatexample.utils

sealed class Resources<T>(val data: T? = null, val message: String? = null) {
    class Success<T>(data: T?): Resources<T>(data = data)
    class Error<T>(message: String?, data: T? = null): Resources<T>(message = message, data = data)
}
