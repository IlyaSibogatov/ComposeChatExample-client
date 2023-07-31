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
    val friends: List<Friend> = listOf(),
    val followers: List<String> = listOf(),
    val friendshipRequests: List<String> = listOf(),
)

data class FriendListUIState(
    val uid: String = "",
    val screenType: String = "",
    val usersList: List<Friend> = listOf(),
    val loadingStatus: Boolean = false,
)