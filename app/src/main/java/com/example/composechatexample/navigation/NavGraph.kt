package com.example.composechatexample.navigation

import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navDeepLink
import com.example.composechatexample.data.preferences.PreferencesManager
import com.example.composechatexample.screens.chat.chatdetails.ChatScreen
import com.example.composechatexample.screens.chat.chatlist.ChatListScreen
import com.example.composechatexample.screens.notifications.NotificationsScreen
import com.example.composechatexample.screens.onboarding.OnBoardingScreen
import com.example.composechatexample.screens.profile.ProfileScreen
import com.example.composechatexample.screens.profile.userlist.UsersListScreen
import com.example.composechatexample.screens.settings.SettingsScreen
import com.example.composechatexample.utils.Constants

@Composable
fun NavGraph(
    preferencesManager: PreferencesManager,
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination =
        if (preferencesManager.userLogged && preferencesManager.uuid != null)
            Constants.CHAT_LIST_ROUTE
        else Constants.ONBOARD_ROUTE
    ) {

        composable(route = BottomNavBar.ChatList.route) {
            ChatListScreen(navController)
        }
        composable(route = BottomNavBar.Profile.route) {
            ProfileScreen(navController)
        }
        composable(route = BottomNavBar.Notifications.route) {
            NotificationsScreen(navController)
        }
        composable(
            route = "${Constants.PROFILE_ROUTE}/{${Constants.USER_UID}}",
            deepLinks = listOf(
                navDeepLink {
                    uriPattern = "http://open_profile/uuid={${Constants.USER_UID}}"
                    action = Intent.ACTION_VIEW
                }
            ),
            arguments = listOf(
                navArgument(Constants.USER_UID) {
                    type = NavType.StringType
                    nullable = true
                    defaultValue = null
                }
            )
        ) { backStackEntry ->
            if (
                backStackEntry.arguments?.getString(Constants.USER_UID) == preferencesManager.uuid ||
                backStackEntry.arguments?.getString(Constants.USER_UID) == null
            )
                navController.navigate(BottomNavBar.Profile.route) {
                    popUpTo(navController.graph.findStartDestination().id)
                    launchSingleTop = true
                }
            else
                ProfileScreen(
                    navController,
                    backStackEntry.arguments?.getString(Constants.USER_UID)
                )
        }
        composable(route = Constants.ONBOARD_ROUTE) {
            OnBoardingScreen(navController)
        }
        composable(route = Constants.SETTINGS_ROUTE) {
            SettingsScreen(navController)
        }
        composable(
            route = "${Constants.FRIENDS_LIST_ROUTE}/{${Constants.USER_UID}}/{${Constants.USERS_TYPE}}",
            arguments = listOf(
                navArgument(Constants.USER_UID) { type = NavType.StringType },
                navArgument(Constants.USERS_TYPE) { type = NavType.StringType }
            )
        ) { backStackEntry ->
            UsersListScreen(
                navController,
                backStackEntry.arguments?.getString(Constants.USER_UID),
                backStackEntry.arguments?.getString(Constants.USERS_TYPE)
            )
        }
        composable(
            route = "${Constants.CHAT_ROUTE}/{${Constants.CHAT_ID}}",
            arguments = listOf(navArgument(Constants.CHAT_ID) { type = NavType.StringType })
        ) { backStackEntry ->
            ChatScreen(
                navController,
                backStackEntry.arguments?.getString(Constants.CHAT_ID)
            )
        }
    }
}