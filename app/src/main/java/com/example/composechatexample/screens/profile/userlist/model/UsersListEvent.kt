package com.example.composechatexample.screens.profile.userlist.model

interface UsersListEvent {
    data class NavigateTo(val route: String) : UsersListEvent

    data class ToastEvent(var msg: String) : UsersListEvent
}