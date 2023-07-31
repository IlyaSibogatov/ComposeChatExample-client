package com.example.composechatexample.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.composechatexample.screens.chat.chatdetails.ChatScreen
import com.example.composechatexample.screens.chat.chatlist.ChatListScreen
import com.example.composechatexample.screens.onboarding.OnBoardingScreen
import com.example.composechatexample.screens.profile.ProfileScreen
import com.example.composechatexample.screens.profile.userlist.UsersListScreen
import com.example.composechatexample.screens.settings.SettingsScreen
import com.example.composechatexample.screens.settings.languages.LanguageScreen
import com.example.composechatexample.utils.Constants

@Composable
fun NavGraph(
    navController: NavHostController,
) {
    NavHost(
        navController = navController,
        startDestination = Constants.CHAT_LIST_ROUTE
    ) {
        composable(route = BottomNavBar.ChatList.route) {
            ChatListScreen(navController)
        }
        composable(route = BottomNavBar.Profile.route) {
            ProfileScreen(navController)
        }
        composable(
            route = "${Constants.PROFILE_ROUTE}/{${Constants.USER_UID}}",
            arguments = listOf(
                navArgument(Constants.USER_UID) { type = NavType.StringType })
        ) { backStackEntry ->
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
        composable(route = Constants.LANGUAGE_ROUTE) {
            LanguageScreen(navController)
        }
    }
}