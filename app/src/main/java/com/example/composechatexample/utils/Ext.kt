package com.example.composechatexample.utils

import android.content.Context
import android.widget.Toast
import java.util.Locale

object Ext {
    fun showToast(context: Context, msg: String) {
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
    }

    fun setLanguage(context: Context, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources = context.resources
        val configuration = resources.configuration
        configuration.locale = locale
        resources.updateConfiguration(configuration, resources.displayMetrics)
    }

    fun getLocale(context: Context): String {
        val resources = context.resources
        return resources.configuration.locale.language
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
}