package com.example.composechatexample.screens.profile.model

interface ProfileScreenEvent {
    data class NavigateTo(val route: String) : ProfileScreenEvent
}