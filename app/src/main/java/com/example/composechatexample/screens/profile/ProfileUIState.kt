package com.example.composechatexample.screens.profile

import android.net.Uri
import com.example.composechatexample.domain.model.Friend
import com.example.composechatexample.domain.model.NewUserInfo
import com.example.composechatexample.utils.ViewForDisplay

data class ProfileUIState(
    val uid: String = "",
    val imageUri: Uri? = null,
    val username: String = "",
    val selfInfo: String = "",
    val showMoreInfo: Boolean = false,
    val errors: ProfileErrors = ProfileErrors(),
    val infoOverflowed: Boolean = false,
    val onlineStatus: Boolean = false,
    val gettingUserError: Boolean = false,
    val updateImage: Boolean = false,
    val lastActionTime: Long? = null,
    val newInfo: NewUserInfo = NewUserInfo(),
    val friends: List<Friend> = listOf(),
    val followers: List<String> = listOf(),
    val friendshipRequests: List<String> = listOf(),
    val displayingView: ViewForDisplay? = null
)

data class FriendListUIState(
    val uid: String = "",
    val screenType: String = "",
    val usersList: List<Friend> = listOf(),
    val loadingStatus: Boolean = false,
)

data class ProfileErrors(
    val userNameNotMatched: Boolean = false,
    val emptyUsername: Boolean = false,
    val newInfoNotChanged: Boolean = false
)