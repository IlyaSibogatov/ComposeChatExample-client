package com.example.composechatexample.data.preferences

import android.content.SharedPreferences
import com.example.composechatexample.data.model.LanguageEntity
import com.example.composechatexample.utils.TypeTheme
import com.google.gson.Gson
import javax.inject.Inject

class PreferencesManager @Inject constructor(
    private val sharedPreferences: SharedPreferences,
) {
    var userLogged: Boolean
        get() = sharedPreferences.getBoolean(USER_LOGGED, false)
        set(value) = sharedPreferences.edit().putBoolean(USER_LOGGED, value).apply()

    var userName: String
        get() = sharedPreferences.getString(USER_NAME, "").toString()
        set(value) = sharedPreferences.edit().putString(USER_NAME, value).apply()

    var theme: String
        get() = sharedPreferences.getString(THEME, TypeTheme.SYSTEM.name).toString()
        set(value) = sharedPreferences.edit().putString(THEME, value).apply()

    var privacy: String
        get() = sharedPreferences.getString(PRIVACY, "Public").toString()
        set(value) = sharedPreferences.edit().putString(PRIVACY, value).apply()

    var notification: Boolean
        get() = sharedPreferences.getBoolean(NOTIFICATION, true)
        set(value) = sharedPreferences.edit().putBoolean(NOTIFICATION, value).apply()

    var notificationScreenOpen: Boolean
        get() = sharedPreferences.getBoolean(NOTIFICATION_SCREEN_OPEN, false)
        set(value) = sharedPreferences.edit().putBoolean(NOTIFICATION_SCREEN_OPEN, value).apply()

    var support: String
        get() = sharedPreferences.getString(SUPPORT, "___@gmail.com").toString()
        set(value) = sharedPreferences.edit().putString(SUPPORT, value).apply()

    var uuid: String?
        get() = sharedPreferences.getString(UUID, null).toString()
        set(value) = sharedPreferences.edit().putString(UUID, value).apply()

    var language: LanguageEntity?
        get() = Gson().fromJson(
            sharedPreferences.getString(LANGUAGE_ARG, null),
            LanguageEntity::class.java
        )
        set(value) = sharedPreferences.edit().putString(LANGUAGE_ARG, Gson().toJson(value)).apply()

    var tokenFcm: String?
        get() = sharedPreferences.getString(TOKEN_FCM, null).toString()
        set(value) = sharedPreferences.edit().putString(TOKEN_FCM, value).apply()

    var deviceId: String?
        get() = sharedPreferences.getString(DEVICE_ID, null).toString()
        set(value) = sharedPreferences.edit().putString(DEVICE_ID, value).apply()

    var deviceType: String?
        get() = sharedPreferences.getString(DEVICE_TYPE, null).toString()
        set(value) = sharedPreferences.edit().putString(DEVICE_TYPE, value).apply()

    fun clearData() {
        val _language = language
        val _tokenFcm = tokenFcm
        val _deviceId = deviceId
        val _deviceType = deviceType
        sharedPreferences.edit().clear().apply()
        language = _language
        tokenFcm = _tokenFcm
        deviceId = _deviceId
        deviceType = _deviceType
    }

    companion object {
        private const val USER_LOGGED = "user_logged"
        private const val USER_NAME = "user_name"
        private const val THEME = "theme"
        private const val PRIVACY = "privacy"
        private const val NOTIFICATION = "notification"
        private const val NOTIFICATION_SCREEN_OPEN = "notification_screen_open"
        private const val SUPPORT = "support"
        private const val UUID = "uuid"
        private const val LANGUAGE_ARG = "user_language_arg"
        private const val TOKEN_FCM = "tokenFcm"
        private const val DEVICE_ID = "device_id"
        private const val DEVICE_TYPE = "device_type"
    }
}