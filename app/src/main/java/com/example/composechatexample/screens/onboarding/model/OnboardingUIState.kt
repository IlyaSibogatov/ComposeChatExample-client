package com.example.composechatexample.screens.onboarding.model

data class OnBoardingUIState(
    val isLoading: Boolean = false,
    val userLogged: Boolean = false,
    val showSignUp: Boolean = false,
    val errors: OnBoardingErrors = OnBoardingErrors(),
    val username: String = "",
    val password: String = "",
)
data class OnBoardingErrors(
    val usernameError: Boolean = false,
    val passwordError: Boolean = false,
    val usernameEmptyError:Boolean = false,
    val passwordEmptyError: Boolean = false,
)