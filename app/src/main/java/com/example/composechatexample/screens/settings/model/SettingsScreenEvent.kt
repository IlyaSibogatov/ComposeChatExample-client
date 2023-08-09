package com.example.composechatexample.screens.settings.model

import com.example.composechatexample.data.model.LanguageEntity
import com.example.composechatexample.utils.TypeTheme

interface SettingsScreenEvent {
    data class NavigateTo(val route: String) : SettingsScreenEvent
    data class ToastEvent(val msg: String) : SettingsScreenEvent
    data class ThemeEvent(val theme: TypeTheme): SettingsScreenEvent

    data class SetLanguage(val language: LanguageEntity) : SettingsScreenEvent
}