package com.example.composechatexample.activities

import androidx.lifecycle.ViewModel
import com.example.composechatexample.data.preferences.PreferencesManager
import com.example.composechatexample.utils.Constants.listLanguages
import com.example.composechatexample.utils.TypeTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
) : ViewModel() {

    fun getLocale(systemLocal: String): String {
        return preferencesManager.language?.languageCode ?: listLanguages.find {
            it.languageCode == systemLocal
        }?.let {
            preferencesManager.language = it
            return it.languageCode
        } ?: listLanguages.find { it.languageCode == ENGLISH_LANGUAGE_CODE }!!.let {
            preferencesManager.language = it
            it.languageCode
        }
    }

    fun getTheme(): TypeTheme {
        return when (preferencesManager.theme) {
            TypeTheme.DARK.name -> TypeTheme.DARK
            TypeTheme.LIGHT.name -> TypeTheme.LIGHT
            else -> TypeTheme.SYSTEM
        }
    }

    fun setNotificationEnabled(enabled: Boolean) {
        preferencesManager.notification = enabled
    }

    companion object {
        private const val ENGLISH_LANGUAGE_CODE = "en"
    }
}