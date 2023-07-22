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
import com.example.composechatexample.utils.eventprovider.EventProvider
import com.example.composechatexample.utils.eventprovider.EventProviderImpl
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
    private val preferencesManager: PreferencesManager
) : ViewModel(), EventProvider<OnboardScreenEvent> by EventProviderImpl() {

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

    fun login() {
        if (uiState.value.username.isBlank() || uiState.value.password.isBlank()) {
            _uiState.value = uiState.value.copy(
                errors = OnBoardingErrors(
                    usernameError = uiState.value.errors.usernameError,
                    passwordError = uiState.value.errors.passwordError,
                    usernameEmptyError = uiState.value.username.isBlank(),
                    passwordEmptyError = uiState.value.password.isBlank(),
                ),
            )
        } else {
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
                            preferencesManager.userName = uiState.value.username
                            preferencesManager.userLogged = true
                        }

                        HttpStatusCode.NoContent.value -> {
                            _uiState.value = uiState.value.copy(
                                errors = OnBoardingErrors(
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
        if (uiState.value.username.isBlank() || uiState.value.password.isBlank()) {
            _uiState.value = uiState.value.copy(
                errors = OnBoardingErrors(
                    usernameError = uiState.value.errors.usernameError,
                    passwordError = uiState.value.errors.passwordError,
                    usernameEmptyError = uiState.value.username.isBlank(),
                    passwordEmptyError = uiState.value.password.isBlank(),
                ),
            )
        } else {
            viewModelScope.launch {
                onboardingService.signup(
                    UserCredentials(
                        username = uiState.value.username,
                        password = uiState.value.password
                    )
                )?.let {
                    when (it.status) {
                        HttpStatusCode.OK.value -> {
                            sendEvent(OnboardScreenEvent.NavigateTo(Constants.CHAT_LIST_ROUTE))
                            preferencesManager.userName = uiState.value.username
                            preferencesManager.userLogged = true
                        }

                        HttpStatusCode.NoContent.value -> {
                            _uiState.value = uiState.value.copy(
                                errors = OnBoardingErrors(
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