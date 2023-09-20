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
import com.example.composechatexample.utils.PassUpdateState
import com.example.composechatexample.utils.ResponseStatus
import com.example.composechatexample.utils.SettingType
import com.example.composechatexample.utils.SettingType.CONFIDENTIALITY
import com.example.composechatexample.utils.SettingType.EDIT_PASSWORD
import com.example.composechatexample.utils.SettingType.LANG
import com.example.composechatexample.utils.SettingType.NOTIFICATION
import com.example.composechatexample.utils.SettingType.PERS_DATA
import com.example.composechatexample.utils.SettingType.THEME
import com.example.composechatexample.utils.SettingsDialogs
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

    fun getNotification(): Boolean = preferencesManager.notification

    fun getPrivacy(): String = preferencesManager.privacy

    fun getMail(): String = preferencesManager.support

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
            _uiState.value = uiState.value.copy(
                dialogs = null
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
        if (uiState.value.dialogs == null) {
            _uiState.value = uiState.value.copy(
                dialogs = SettingsDialogs.PASS
            )
        } else {
            _uiState.value = uiState.value.copy(
                dialogs = null,
                pass = PassState(),
                errors = PassErrors()
            )
        }
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
                currentField = PassUpdateState.FIELD_CORRECTLY,
                newField = uiState.value.errors.newField,
                repeatedField = uiState.value.errors.repeatedField,
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
                currentField = uiState.value.errors.currentField,
                newField = PassUpdateState.FIELD_CORRECTLY,
                repeatedField = uiState.value.errors.repeatedField,
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
                currentField = uiState.value.errors.currentField,
                newField = uiState.value.errors.newField,
                repeatedField = PassUpdateState.FIELD_CORRECTLY,
            )
        )
    }

    fun showVerificationDialog() {
        if (uiState.value.dialogs == null) {
            _uiState.value = uiState.value.copy(
                dialogs = SettingsDialogs.LOG_OUT
            )
        } else {
            _uiState.value = uiState.value.copy(
                dialogs = null
            )
        }
    }

    private fun checkFields() {
        val errors = PassErrors()
        viewModelScope.launch {
            validator.isValidPassword(uiState.value.pass.currentPass).let {
                errors.currentField = when (it) {
                    "pattern_not_matched" -> PassUpdateState.NOT_MATCH_PATTERN
                    "empty_field" -> PassUpdateState.EMPTY_FIELD
                    "password_is_ok" -> PassUpdateState.FIELD_CORRECTLY
                    else -> PassUpdateState.FIELD_CORRECTLY
                }
            }
        }
        viewModelScope.launch {
            validator.isValidPassword(uiState.value.pass.newPass).let {
                errors.newField = when (it) {
                    "pattern_not_matched" -> PassUpdateState.NOT_MATCH_PATTERN
                    "empty_field" -> PassUpdateState.EMPTY_FIELD
                    "password_is_ok" -> PassUpdateState.FIELD_CORRECTLY
                    else -> PassUpdateState.FIELD_CORRECTLY
                }
            }
        }
        viewModelScope.launch {
            validator.isValidPassword(uiState.value.pass.repeatedPass).let {
                errors.repeatedField = when (it) {
                    "pattern_not_matched" -> PassUpdateState.NOT_MATCH_PATTERN
                    "empty_field" -> PassUpdateState.EMPTY_FIELD
                    "password_is_ok" -> PassUpdateState.FIELD_CORRECTLY
                    else -> PassUpdateState.FIELD_CORRECTLY
                }
            }
        }

        if (
            uiState.value.pass.currentPass == uiState.value.pass.newPass &&
            errors.currentField == PassUpdateState.FIELD_CORRECTLY
        ) {
            errors.currentField = PassUpdateState.NEW_CURRENT_SAME
            errors.newField = PassUpdateState.NEW_CURRENT_SAME
        }

        if (errors.newField != PassUpdateState.NOT_MATCH_PATTERN &&
            errors.repeatedField != PassUpdateState.NOT_MATCH_PATTERN &&
            (uiState.value.pass.newPass != uiState.value.pass.repeatedPass) &&
            (errors.newField != PassUpdateState.EMPTY_FIELD &&
                    errors.repeatedField != PassUpdateState.EMPTY_FIELD)
        ) {
            errors.newField = PassUpdateState.NEWEST_NOT_SAME
            errors.repeatedField = PassUpdateState.NEWEST_NOT_SAME
        }
        _uiState.value = uiState.value.copy(
            errors = errors
        )
    }

    fun updatePass() {
        checkFields()
        with(uiState.value.errors) {
            if (
                this.currentField == PassUpdateState.FIELD_CORRECTLY &&
                this.newField == PassUpdateState.FIELD_CORRECTLY &&
                this.repeatedField == PassUpdateState.FIELD_CORRECTLY
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
                                        currentField = PassUpdateState.CURRENT_NOT_EQ_OLD
                                    )
                                )
                            }

                            "new with old same" -> {
                                _uiState.value = uiState.value.copy(
                                    errors = PassErrors(
                                        newField = PassUpdateState.NEW_EQ_OLD
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