package com.example.composechatexample.screens.settings.languages.model

import com.example.composechatexample.data.model.LanguageEntity

data class LanguageUIState(
    var selectedLanguage: LanguageEntity? = null,
    var languages: List<LanguageEntity> = listOf(),
)