package com.example.composechatexample.screens.settings.languages

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composechatexample.data.model.LanguageEntity
import com.example.composechatexample.data.preferences.PreferencesManager
import com.example.composechatexample.screens.settings.languages.model.LanguageUIState
import com.example.composechatexample.screens.settings.languages.model.LanguagesScreenEvent
import com.example.composechatexample.utils.Constants.listLanguages
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LanguagesViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
) : ViewModel() {

    private val _uiState = MutableStateFlow(LanguageUIState())
    val uiState: StateFlow<LanguageUIState> = _uiState

    private val eventChannel = Channel<LanguagesScreenEvent>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    init {
        _uiState.value = uiState.value.copy(
            selectedLanguage = preferencesManager.language
                ?: listLanguages.find { it.languageCode == "en" },
            languages = listLanguages,
        )
    }

    fun onLanguageClick(language: LanguageEntity) {
        preferencesManager.language = language
        _uiState.value = uiState.value.copy(
            selectedLanguage = language
        )
        sendEvent(LanguagesScreenEvent.SetLanguage(language))
    }

    private fun sendEvent(event: LanguagesScreenEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}