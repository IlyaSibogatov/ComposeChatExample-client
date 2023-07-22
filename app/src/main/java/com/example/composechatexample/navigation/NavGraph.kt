package com.example.composechatexample.navigation

import android.os.Build
import androidx.annotation.RequiresApi
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
import com.example.composechatexample.screens.profile.friendslist.FriendsListScreen
import com.example.composechatexample.screens.settings.SettingsScreen
import com.example.composechatexample.screens.settings.languages.LanguageScreen
import com.example.composechatexample.utils.Constants

@RequiresApi(Build.VERSION_CODES.N)
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
        composable(route = Constants.ONBOARD_ROUTE) {
            OnBoardingScreen(navController)
        }
        composable(route = Constants.SETTINGS_ROUTE) {
            SettingsScreen(navController)
        }
        composable(route = Constants.FRIENDS_LIST_ROUTE) {
            FriendsListScreen(navController)
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