package com.example.composechatexample.navigation

import com.example.composechatexample.R
import com.example.composechatexample.utils.Constants

sealed class BottomNavBar(
    val route: String,
    val title: Int,
    val icon: Int,
) {
    object ChatList : BottomNavBar(
        route = Constants.CHAT_LIST_ROUTE,
        title = R.string.chat_list_title,
        icon = R.drawable.ic_chat_list,
    )

    object Profile : BottomNavBar(
        route = Constants.PROFILE_ROUTE,
        title = R.string.profile_title,
        icon = R.drawable.ic_profile,
    )

    object Notifications : BottomNavBar(
        route = Constants.NOTIFICATIONS_ROUTE,
        title = R.string.notifications_title,
        icon = R.drawable.ic_notifications,
    )

    object Settings : BottomNavBar(
        route = Constants.SETTINGS_ROUTE,
        title = R.string.settings_title,
        icon = R.drawable.ic_settings,
    )
}

data class Screen(
    val route: String,
    val title: String,
)