package com.example.composechatexample.navigation

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.composechatexample.R
import com.example.composechatexample.utils.Constants

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainEntryPoint() {
    val navController = rememberNavController()

    val screensToShowBottomBar = listOf(
        BottomNavBar.ChatList,
        BottomNavBar.Profile,
        BottomNavBar.Settings,
    )

    val screens = listOf(
        Screen(Constants.CHAT_LIST_ROUTE, Constants.CHAT_LIST_TITLE),
        Screen(Constants.CHAT_ROUTE + "/{${Constants.CHAT_ID}}", Constants.CHAT_TITLE),
        Screen(Constants.PROFILE_ROUTE, Constants.PROFILE_TITLE),
        Screen(Constants.CREATE_CHAT_ROUTE, Constants.CREATE_CHAT_TITLE),
        Screen(Constants.FRIENDS_LIST_ROUTE, Constants.FRIENDS_LIST_TITLE)
    )

    val newBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = newBackStackEntry?.destination

    val showBottomBar =
        navController.currentBackStackEntryAsState()
            .value?.destination?.route in screensToShowBottomBar.map { it.route }

    Scaffold(
        topBar = {
            if (!showBottomBar && screens.find {
                    currentDestination?.route.toString() == (it.route)
                } != null) {
                TopAppBar(
                    title = {
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (
                                currentDestination?.route.toString() !=
                                Constants.CHAT_LIST_ROUTE
                            ) {
                                IconButton(onClick = {
                                    navController.popBackStack()
                                }) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_arrow_back),
                                        contentDescription = Constants.CONTENT_DESCRIPTION,
                                        tint = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            }
                            Text(
                                text = screens.find {
                                    currentDestination?.route.toString() == (it.route)
                                }!!.title,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.smallTopAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                )
            }
        },
        bottomBar = {
            if (showBottomBar) {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    screensToShowBottomBar.forEach { screen ->
                        NavigationBarItem(
                            label = {
                                Text(
                                    text = stringResource(id = screen.title),
                                    color = if (isCurrentDestination(
                                            currentDestination,
                                            screen.route
                                        )
                                    )
                                        MaterialTheme.colorScheme.onPrimary
                                    else MaterialTheme.colorScheme.surface
                                )
                            },
                            icon = {
                                Icon(
                                    painterResource(id = screen.icon),
                                    contentDescription = Constants.CONTENT_DESCRIPTION,
                                    tint = if (isCurrentDestination(
                                            currentDestination,
                                            screen.route
                                        )
                                    )
                                        MaterialTheme.colorScheme.onPrimaryContainer
                                    else MaterialTheme.colorScheme.surface
                                )
                            },
                            selected = isCurrentDestination(currentDestination, screen.route),
                            onClick = {
                                if (navController.currentDestination?.route != screen.route) {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id)
                                        launchSingleTop = true
                                    }
                                }
                            },
                        )
                    }
                }
            }
        }
    ) { contentPadding ->
        ConstraintLayout(
            modifier = Modifier
                .fillMaxWidth()
                .padding(contentPadding)
        ) {
            NavGraph(
                navController = navController
            )
        }
    }
}

private fun isCurrentDestination(currentDestination: NavDestination?, route: String): Boolean {
    return currentDestination?.hierarchy?.any { it.route == route } == true
}