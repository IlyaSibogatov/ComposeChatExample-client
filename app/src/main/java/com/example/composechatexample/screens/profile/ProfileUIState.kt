package com.example.composechatexample.screens.profile

import com.example.composechatexample.domain.model.Friend

data class ProfileUIState(
    val username: String = "",
    val name: String = "___",
    val number: String = "+# (###) ###-##-##",
    val email: String = "___@gmail.com",
    val canEditProfile: Boolean = false,
    val friendList: List<Friend> = listOf(
        Friend(
            isOnline = false,
            username = "Friend 1",
        ),
        Friend(
            isOnline = true,
            username = "Friend 2",
        ),
        Friend(
            isOnline = true,
            username = "Friend 3",
        ),
        Friend(
            isOnline = false,
            username = "Friend 4",
        ),
        Friend(
            isOnline = true,
            username = "Friend 5",
        ),
        Friend(
            isOnline = true,
            username = "Friend 6",
        )
    )
)