package com.example.composechatexample.screens.profile

import com.example.composechatexample.domain.model.Friend

data class ProfileUIState(
    val uid: String = "",
    val username: String = "",
    val selfInfo: String = "",
    val infoOverflowed: Boolean = false,
    val onlineStatus: Boolean = false,
    val loadingStatus: Boolean = false,
    val gettingUserError: Boolean = false,
    val lastActionTime: Long? = null,
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