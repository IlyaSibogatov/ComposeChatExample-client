package com.example.composechatexample.screens.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composechatexample.data.preferences.PreferencesManager
import com.example.composechatexample.data.remote.OnboardingService
import com.example.composechatexample.domain.model.UserCredentials
import com.example.composechatexample.screens.onboarding.model.OnBoardingErrors
import com.example.composechatexample.screens.onboarding.model.OnBoardingUIState
import com.example.composechatexample.screens.onboarding.model.OnboardScreenEvent
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.Validator
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnBoardingViewModel @Inject constructor(
    private val onboardingService: OnboardingService,
    private val preferencesManager: PreferencesManager,
    private val validator: Validator
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnBoardingUIState())
    val uiState: StateFlow<OnBoardingUIState> = _uiState

    private val eventChannel = Channel<OnboardScreenEvent>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    init {
        _uiState.value = uiState.value.copy(
            userLogged = preferencesManager.userLogged
        )
    }

    fun updateUsername(newText: String) {
        _uiState.value = uiState.value.copy(
            username = newText,
            errors = OnBoardingErrors(
                userNameExistError = false,
                usernameError = false,
                passwordError = uiState.value.errors.passwordError,
                usernameEmptyError = false,
                passwordEmptyError = uiState.value.errors.passwordEmptyError,
            ),
        )
    }

    fun updatePassword(newText: String) {
        _uiState.value = uiState.value.copy(
            password = newText,
            errors = OnBoardingErrors(
                usernameError = uiState.value.errors.usernameError,
                passwordError = false,
                usernameEmptyError = uiState.value.errors.usernameEmptyError,
                passwordEmptyError = false,
            ),
        )
    }

    fun checkFields() {
        viewModelScope.launch {
            validator.isValidUserName(uiState.value.username).let {
                when (it) {
                    "pattern_not_matched" -> {
                        _uiState.value = uiState.value.copy(
                            errors = OnBoardingErrors(
                                userNameExistError = false,
                                usernameError = true,
                                passwordError = uiState.value.errors.passwordError,
                                usernameEmptyError = false,
                                passwordEmptyError = uiState.value.errors.passwordEmptyError,
                            ),
                        )
                    }

                    "empty_field" -> {
                        _uiState.value = uiState.value.copy(
                            errors = OnBoardingErrors(
                                userNameExistError = false,
                                usernameError = false,
                                passwordError = uiState.value.errors.passwordError,
                                usernameEmptyError = true,
                                passwordEmptyError = uiState.value.errors.passwordEmptyError,
                            ),
                        )
                    }

                    "username_is_ok" -> {
                        _uiState.value = uiState.value.copy(
                            errors = OnBoardingErrors(
                                userNameExistError = false,
                                usernameError = false,
                                passwordError = uiState.value.errors.passwordError,
                                usernameEmptyError = false,
                                passwordEmptyError = uiState.value.errors.passwordEmptyError,
                            ),
                        )
                    }
                }
            }
        }
        viewModelScope.launch {
            validator.isValidPassword(uiState.value.password).let {
                when (it) {
                    "pattern_not_matched" -> {
                        _uiState.value = uiState.value.copy(
                            errors = OnBoardingErrors(
                                userNameExistError = uiState.value.errors.userNameExistError,
                                usernameError = uiState.value.errors.usernameError,
                                passwordError = true,
                                usernameEmptyError = uiState.value.errors.usernameEmptyError,
                                passwordEmptyError = false,
                            ),
                        )
                    }

                    "empty_field" -> {
                        _uiState.value = uiState.value.copy(
                            errors = OnBoardingErrors(
                                userNameExistError = uiState.value.errors.userNameExistError,
                                usernameError = uiState.value.errors.usernameError,
                                passwordError = false,
                                usernameEmptyError = uiState.value.errors.usernameEmptyError,
                                passwordEmptyError = true,
                            ),
                        )
                    }

                    "password_is_ok" -> {
                        _uiState.value = uiState.value.copy(
                            errors = OnBoardingErrors(
                                userNameExistError = uiState.value.errors.userNameExistError,
                                usernameError = uiState.value.errors.usernameError,
                                passwordError = false,
                                usernameEmptyError = uiState.value.errors.usernameEmptyError,
                                passwordEmptyError = false,
                            ),
                        )
                    }
                }
            }
        }
    }

    fun login() {
        checkFields()
        if (
            !uiState.value.errors.usernameError && !uiState.value.errors.passwordError &&
            !uiState.value.errors.usernameEmptyError && !uiState.value.errors.passwordEmptyError
        ) {
            viewModelScope.launch {
                onboardingService.login(
                    UserCredentials(
                        username = uiState.value.username,
                        password = uiState.value.password
                    )
                )?.let {
                    when (it.status) {
                        HttpStatusCode.OK.value -> {
                            sendEvent(OnboardScreenEvent.NavigateTo(Constants.CHAT_LIST_ROUTE))
                            preferencesManager.uuid = it.msg
                            preferencesManager.userName = uiState.value.username
                            preferencesManager.userLogged = true
                        }

                        HttpStatusCode.NoContent.value -> {
                            _uiState.value = uiState.value.copy(
                                errors = OnBoardingErrors(
                                    userNameExistError = uiState.value.errors.userNameExistError,
                                    usernameError = true,
                                    passwordError = true,
                                    usernameEmptyError = uiState.value.errors.usernameEmptyError,
                                    passwordEmptyError = uiState.value.errors.passwordEmptyError,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

    fun signup() {
        checkFields()
        if (
            !uiState.value.errors.usernameError && !uiState.value.errors.passwordError &&
            !uiState.value.errors.usernameEmptyError && !uiState.value.errors.passwordEmptyError
        ) {
            viewModelScope.launch {
                onboardingService.signup(
                    UserCredentials(
                        username = uiState.value.username,
                        password = uiState.value.password
                    )
                )?.let {
                    when {
                        it.msg == "username_exist" -> {
                            _uiState.value = uiState.value.copy(
                                errors = OnBoardingErrors(
                                    userNameExistError = true,
                                    usernameError = false,
                                    passwordError = uiState.value.errors.passwordError,
                                    usernameEmptyError = uiState.value.errors.usernameEmptyError,
                                    passwordEmptyError = uiState.value.errors.passwordEmptyError,
                                ),
                            )
                        }

                        it.status == HttpStatusCode.OK.value -> {
                            sendEvent(OnboardScreenEvent.NavigateTo(Constants.CHAT_LIST_ROUTE))
                            preferencesManager.uuid = it.msg
                            preferencesManager.userName = uiState.value.username
                            preferencesManager.userLogged = true
                        }

                        it.status == HttpStatusCode.NoContent.value -> {
                            _uiState.value = uiState.value.copy(
                                errors = OnBoardingErrors(
                                    userNameExistError = uiState.value.errors.userNameExistError,
                                    usernameError = true,
                                    passwordError = true,
                                    usernameEmptyError = uiState.value.errors.usernameEmptyError,
                                    passwordEmptyError = uiState.value.errors.passwordEmptyError,
                                ),
                            )
                        }
                    }
                }
            }
        }
    }

    fun changeState() {
        _uiState.value = uiState.value.copy(
            showSignUp = !uiState.value.showSignUp
        )
    }

    private fun sendEvent(event: OnboardScreenEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}