package com.example.composechatexample.screens.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composechatexample.data.preferences.PreferencesManager
import com.example.composechatexample.data.remote.OnboardingService
import com.example.composechatexample.screens.settings.model.PassErrors
import com.example.composechatexample.screens.settings.model.PassState
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
import com.example.composechatexample.utils.Validator
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
    private val preferencesManager: PreferencesManager,
    private val validator: Validator
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUIState())
    val uiState: StateFlow<SettingsUIState> = _uiState

    private val eventChannel = Channel<SettingsScreenEvent>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    val listAppSetting = listOf(LANG, THEME, NOTIFICATION)
    val listAccSetting = listOf(CONFIDENTIALITY, PERS_DATA, EDIT_PASSWORD)

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
            EDIT_PASSWORD -> {
                showChangePassDialog()
            }
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

    fun showChangePassDialog() {
        _uiState.value = uiState.value.copy(
            showPassDialog = !uiState.value.showPassDialog
        )
        if (!uiState.value.showPassDialog)
            _uiState.value = uiState.value.copy(
                pass = PassState(),
                errors = PassErrors()
            )
    }

    fun clearPassField(field: String) {
        when (field) {
            "current" -> {
                if (uiState.value.pass.currentPass.isNotEmpty()) {
                    changeCurrentPass("")
                }
            }

            "new" -> {
                if (uiState.value.pass.newPass.isNotEmpty()) {
                    changeNewPass("")
                }
            }

            "repeat" -> {
                if (uiState.value.pass.repeatedPass.isNotEmpty()) {
                    changeRepeatedPass("")
                }
            }
        }
    }

    fun changeCurrentPass(value: String) {
        _uiState.value = uiState.value.copy(
            pass = PassState(
                currentPass = value,
                newPass = uiState.value.pass.newPass,
                repeatedPass = uiState.value.pass.repeatedPass,
            ),
            errors = PassErrors(
                currentNewSame = false,
                newestNotSame = uiState.value.errors.newestNotSame,
                currentNotMatch = false,
                newNotMatch = uiState.value.errors.newNotMatch,
                repeatNotMatch = uiState.value.errors.repeatNotMatch,
                currentIsEmpty = false,
                newIsEmpty = uiState.value.errors.newIsEmpty,
                repeatIsEmpty = uiState.value.errors.repeatIsEmpty,
            )
        )
    }

    fun changeNewPass(value: String) {
        _uiState.value = uiState.value.copy(
            pass = PassState(
                currentPass = uiState.value.pass.currentPass,
                newPass = value,
                repeatedPass = uiState.value.pass.repeatedPass,
            ),
            errors = PassErrors(
                currentNewSame = false,
                newestNotSame = false,
                currentNotMatch = uiState.value.errors.currentNotMatch,
                newNotMatch = false,
                repeatNotMatch = uiState.value.errors.repeatNotMatch,
                currentIsEmpty = uiState.value.errors.currentIsEmpty,
                newIsEmpty = false,
                repeatIsEmpty = uiState.value.errors.repeatIsEmpty,
            )
        )
    }

    fun changeRepeatedPass(value: String) {
        _uiState.value = uiState.value.copy(
            pass = PassState(
                currentPass = uiState.value.pass.currentPass,
                newPass = uiState.value.pass.newPass,
                repeatedPass = value,
            ),
            errors = PassErrors(
                currentNewSame = uiState.value.errors.currentNewSame,
                newestNotSame = false,
                currentNotMatch = uiState.value.errors.currentNotMatch,
                newNotMatch = uiState.value.errors.newNotMatch,
                repeatNotMatch = false,
                currentIsEmpty = uiState.value.errors.currentIsEmpty,
                newIsEmpty = uiState.value.errors.newIsEmpty,
                repeatIsEmpty = false,
            )
        )
    }

    private fun checkFields() {
        val errors = PassErrors()
        viewModelScope.launch {
            validator.isValidPassword(uiState.value.pass.currentPass).let {
                when (it) {
                    "pattern_not_matched" -> {
                        errors.currentNotMatch = true
                    }

                    "empty_field" -> {
                        errors.currentIsEmpty = true
                    }

                    "password_is_ok" -> {
                        errors.apply {
                            currentNotMatch = false
                            currentIsEmpty = false
                        }
                    }
                }
            }
        }
        viewModelScope.launch {
            validator.isValidPassword(uiState.value.pass.newPass).let {
                when (it) {
                    "pattern_not_matched" -> {
                        errors.newNotMatch = true
                    }

                    "empty_field" -> {
                        errors.newIsEmpty = true
                    }

                    "password_is_ok" -> {
                        errors.apply {
                            repeatNotMatch = false
                            repeatIsEmpty = false
                        }
                    }
                }
            }
        }
        viewModelScope.launch {
            validator.isValidPassword(uiState.value.pass.repeatedPass).let {
                when (it) {
                    "pattern_not_matched" -> {
                        errors.repeatNotMatch = true
                    }

                    "empty_field" -> {
                        errors.repeatIsEmpty = true
                    }

                    "password_is_ok" -> {
                        errors.apply {
                            repeatNotMatch = false
                            repeatIsEmpty = false
                        }
                    }
                }
            }
        }
        errors.currentNewSame =
            uiState.value.pass.newPass == uiState.value.pass.currentPass
                    && uiState.value.pass.currentPass.isNotEmpty()
                    && (!uiState.value.errors.currentNotMatch ||
                    !uiState.value.errors.newNotMatch)
        errors.newestNotSame = (!errors.newNotMatch && !errors.repeatNotMatch) &&
                (uiState.value.pass.newPass != uiState.value.pass.repeatedPass) &&
                (!errors.newIsEmpty && !errors.repeatIsEmpty)
        _uiState.value = uiState.value.copy(
            errors = errors
        )
    }

    fun updatePass() {
        checkFields()
        with(uiState.value.errors) {
            if (!this.currentNewSame && !this.newestNotSame &&
                !this.currentNotMatch && !this.newNotMatch &&
                !this.repeatNotMatch && !this.currentIsEmpty &&
                !this.newIsEmpty && !this.repeatIsEmpty
            ) {
                viewModelScope.launch {
                    onboardingService.changePass(
                        uiState.value.pass.currentPass, uiState.value.pass.newPass,
                        preferencesManager.uuid!!
                    )?.let {
                        when (it) {
                            "current with old not same" -> {
                                _uiState.value = uiState.value.copy(
                                    errors = PassErrors(
                                        currentNotEqOld = true
                                    )
                                )
                            }

                            "new with old same" -> {
                                _uiState.value = uiState.value.copy(
                                    errors = PassErrors(
                                        newEqOld = true
                                    )
                                )
                            }

                            "pass changed" -> {
                                sendEvent(
                                    SettingsScreenEvent.ToastEvent(
                                        ResponseStatus.SUCCESS.value
                                    )
                                )
                                showChangePassDialog()
                            }

                            else -> {
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
        }
    }

    private fun sendEvent(event: SettingsScreenEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}