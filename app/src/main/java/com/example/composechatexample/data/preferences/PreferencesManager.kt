package com.example.composechatexample.data.preferences

import android.content.SharedPreferences
import com.example.composechatexample.data.model.LanguageEntity
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
        get() = sharedPreferences.getString(THEME, "Light").toString()
        set(value) = sharedPreferences.edit().putString(THEME, value).apply()

    var privacy: String
        get() = sharedPreferences.getString(PRIVACY, "Public").toString()
        set(value) = sharedPreferences.edit().putString(PRIVACY, value).apply()

    var notification: String
        get() = sharedPreferences.getString(NOTIFICATION, "Enabled").toString()
        set(value) = sharedPreferences.edit().putString(NOTIFICATION, value).apply()

    var support: String
        get() = sharedPreferences.getString(SUPPORT, "___@gmail.com").toString()
        set(value) = sharedPreferences.edit().putString(SUPPORT, value).apply()


    var language: LanguageEntity?
        get() = Gson().fromJson(
            sharedPreferences.getString(LANGUAGE_ARG, null),
            LanguageEntity::class.java
        )
        set(value) = sharedPreferences.edit().putString(LANGUAGE_ARG, Gson().toJson(value)).apply()

    fun clearData() {
        sharedPreferences.edit().clear().apply()
    }

    companion object {
        private const val USER_LOGGED = "user_logged"
        private const val USER_NAME = "user_name"
        private const val THEME = "theme"
        private const val PRIVACY = "privacy"
        private const val NOTIFICATION = "notification"
        private const val SUPPORT = "support"
        private const val LANGUAGE_ARG = "user_language_arg"
    }
}