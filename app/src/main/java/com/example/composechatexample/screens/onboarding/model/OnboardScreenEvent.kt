package com.example.composechatexample.screens.onboarding.model

sealed interface OnboardScreenEvent {
    data class NavigateTo(val route: String) : OnboardScreenEvent
}