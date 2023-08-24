package com.example.composechatexample.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composechatexample.data.preferences.PreferencesManager
import com.example.composechatexample.data.remote.OnboardingService
import com.example.composechatexample.screens.settings.model.SettingsScreenEvent
import com.example.composechatexample.screens.settings.model.SettingsUIState
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.Constants.listLanguages
import com.example.composechatexample.utils.ResponseStatus
import com.example.composechatexample.utils.SettingType
import com.example.composechatexample.utils.SettingType.CONFIDENTIALITY
import com.example.composechatexample.utils.SettingType.EDIT_PASSWORD
import com.example.composechatexample.utils.SettingType.LANG
import com.example.composechatexample.utils.SettingType.NOTIFICATION
import com.example.composechatexample.utils.SettingType.PERS_DATA
import com.example.composechatexample.utils.SettingType.THEME
import com.example.composechatexample.utils.TypeLang
import com.example.composechatexample.utils.TypeLang.ENG
import com.example.composechatexample.utils.TypeLang.RU
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

    val listAppSetting = listOf(LANG,THEME,NOTIFICATION)
    val listAccSetting = listOf(CONFIDENTIALITY,PERS_DATA,EDIT_PASSWORD)

    fun init() {
        with(preferencesManager) {
            if (this.language == null) {
                listLanguages.find {
                    it.languageCode == Locale.getDefault().language
                }?.let {
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

    fun getLanguage(): String = preferencesManager.language?.languageValue!!

    fun getTheme(): String = preferencesManager.theme

    fun onClickItem(type: Pair<SettingType, Any>) {
        when (type.first) {
            LANG -> {
                if (type.second is TypeLang) {
                    onLanguageClick(type.second as TypeLang)
                }
            }

            THEME -> {
                if (type.second is TypeTheme) {
                    saveTheme(type.second as TypeTheme)
                }
            }

            NOTIFICATION -> {}
            PERS_DATA -> {}
            CONFIDENTIALITY -> {}
            EDIT_PASSWORD -> {}
        }
    }

    private fun onLanguageClick(language: TypeLang) {
        val langEntity = when (language) {
            RU -> listLanguages.first { it.languageCode == "ru" }
            ENG -> listLanguages.first { it.languageCode == "en" }
        }
        preferencesManager.language = langEntity
        _uiState.value = uiState.value.copy(
            language = langEntity.languageValue
        )
        sendEvent(SettingsScreenEvent.SetLanguage(langEntity))
    }

    fun userLogOut() {
        viewModelScope.launch {
            onboardingService.logout(preferencesManager.uuid!!)?.let {
                when (it.status) {
                    HttpStatusCode.OK.value -> {
                        preferencesManager.clearData()
                        sendEvent(
                            SettingsScreenEvent.NavigateTo(
                                Constants.ONBOARD_ROUTE
                            )
                        )
                    }

                    HttpStatusCode.NoContent.value -> {
                        sendEvent(
                            SettingsScreenEvent.ToastEvent(
                                ResponseStatus.FAILED.value
                            )
                        )
                    }
                }
            } ?: sendEvent(
                SettingsScreenEvent.ToastEvent(
                    ResponseStatus.FAILED.value
                )
            )
        }
    }

    private fun saveTheme(type: TypeTheme) {
        preferencesManager.theme = type.name
        _uiState.value = uiState.value.copy(
            theme = type.name
        )
        sendEvent(SettingsScreenEvent.ThemeEvent(type))
    }

    private fun sendEvent(event: SettingsScreenEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}