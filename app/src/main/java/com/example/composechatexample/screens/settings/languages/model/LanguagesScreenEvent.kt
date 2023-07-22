package com.example.composechatexample.screens.settings.languages.model

import com.example.composechatexample.data.model.LanguageEntity

interface LanguagesScreenEvent {

    data class SetLanguage(val language: LanguageEntity) : LanguagesScreenEvent
}