package com.example.composechatexample.screens.settings.model

interface SettingsScreenEvent {
    data class NavigateTo(val route: String) : SettingsScreenEvent
    data class ToastEvent(val msg: String) : SettingsScreenEvent
}