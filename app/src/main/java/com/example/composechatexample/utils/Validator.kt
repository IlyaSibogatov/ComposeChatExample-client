package com.example.composechatexample.utils

import java.util.regex.Pattern

class Validator {
    private val usernamePattern = Pattern.compile(
        "^" +
                "[0-9a-zA-Z]{6,}" +
                "$"
    )

    private val passwordPattern = Pattern.compile(
        "^" +
                "(?=.*[a-z])" +
                "(?=.*[A-Z])" +
                "(?=.*[0-9])" +
                "[0-9a-zA-Z]{6,}" +
                "$"
    )

    fun isValidUserName(value: String): String {
        return when {
            value.isEmpty() -> {
                "empty_field"
            }

            !usernamePattern.matcher(value).matches() -> {
                "pattern_not_matched"
            }

            else -> {
                "username_is_ok"
            }
        }
    }

    fun isValidPassword(value: String): String {
        return when {
            value.isEmpty() -> {
                "empty_field"
            }

            !passwordPattern.matcher(value).matches() -> {
                "pattern_not_matched"
            }

            else -> {
                "password_is_ok"
            }
        }
    }
}