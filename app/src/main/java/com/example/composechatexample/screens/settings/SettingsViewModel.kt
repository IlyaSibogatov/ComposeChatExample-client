package com.example.composechatexample.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composechatexample.data.preferences.PreferencesManager
import com.example.composechatexample.data.remote.OnboardingService
import com.example.composechatexample.screens.settings.model.SettingsScreenEvent
import com.example.composechatexample.screens.settings.model.SettingsUIState
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.Constants.listLanguages
import com.example.composechatexample.utils.TypeTheme
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val onboardingService: OnboardingService,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUIState())
    val uiState: StateFlow<SettingsUIState> = _uiState

    private val eventChannel = Channel<SettingsScreenEvent>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun init() {
        with(preferencesManager) {
            if (this.language == null) {
                listLanguages.find { it.languageCode == Locale.getDefault().language }?.let {
                    this.language = it
                } ?: let {
                    this.language = listLanguages.find { it.languageCode == "en" }
                }
            }
            _uiState.value = uiState.value.copy(
                language = language!!.languageValue,
                theme = theme,
                privacy = privacy,
                notification = notification,
                support = support,
            )
        }
    }

    fun onLanguageClick() {
        sendEvent(SettingsScreenEvent.NavigateTo(Constants.LANGUAGE_ROUTE))
    }

    fun userLogOut() {
        viewModelScope.launch {
            onboardingService.logout(preferencesManager.uuid!!)?.let {
                when (it.status) {
                    HttpStatusCode.OK.value -> {
                        preferencesManager.clearData()
                        sendEvent(SettingsScreenEvent.NavigateTo(Constants.ONBOARD_ROUTE))
                    }

                    HttpStatusCode.NoContent.value -> {
                        /** Show error */
                    }
                }
            }
        }
    }

    fun saveTheme(type: TypeTheme) {
        preferencesManager.theme = type.name
    }

    private fun sendEvent(event: SettingsScreenEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}