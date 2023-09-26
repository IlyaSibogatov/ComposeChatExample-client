@file:OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3Api::class)

package com.example.composechatexample.screens.settings

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.example.composechatexample.R
import com.example.composechatexample.components.ShowMenu
import com.example.composechatexample.screens.dialogs.ChangePasswordDialog
import com.example.composechatexample.screens.dialogs.VerificationDialog
import com.example.composechatexample.screens.settings.model.SettingsScreenEvent
import com.example.composechatexample.ui.theme.themeState
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.Ext
import com.example.composechatexample.utils.ResponseStatus
import com.example.composechatexample.utils.SettingType
import com.example.composechatexample.utils.SettingType.*
import com.example.composechatexample.utils.SettingsDialogs
import com.example.composechatexample.utils.TypeTheme
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SettingsScreen(
    navController: NavHostController,
) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    Scaffold(
        bottomBar = {
            Column {
                SettingItem(
                    title = R.string.tech_support_label,
                    value = uiState.value.support ?: viewModel.getMail()
                ) {
                    val intent = Intent(Intent.ACTION_SENDTO)
                    intent.data = Uri.parse("mailto:" + uiState.value.support)
                    intent.putExtra(
                        Intent.EXTRA_SUBJECT,
                        context.resources.getString(R.string.bug_report_label)
                    )
                    context.startActivity(
                        Intent.createChooser(
                            intent,
                            context.resources.getString(R.string.send_report_label)
                        )
                    )
                }
                SettingItem(
                    title = R.string.exit_label,
                    icon = { SettingIcon(id = R.drawable.ic_logout) },
                    onClick = { viewModel.showDialog(SettingsDialogs.LOG_OUT) }
                )
            }
        }
    ) { padding ->
        when (uiState.value.dialogs) {
            SettingsDialogs.LOG_OUT -> {
                VerificationDialog(
                    text = stringResource(id = R.string.exit_warning),
                    acceptOnClick = (
                            viewModel::userLogOut
                            ),
                    declinedOnClick = ({ viewModel.showDialog() })
                )
            }

            SettingsDialogs.DELETE_ACCOUNT -> {
                VerificationDialog(
                    text = stringResource(id = R.string.delete_account_warning),
                    acceptOnClick = (viewModel::deleteAccount),
                    declinedOnClick = ({ viewModel.showDialog() })
                )
            }

            SettingsDialogs.PASS -> {
                ChangePasswordDialog()
            }

            else -> {}
        }
        LazyColumn(
            modifier = Modifier
                .padding(padding)
                .fillMaxHeight()
        ) {
            item {
                HeaderSetting(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    id = R.string.application_label,
                    colorContainer = MaterialTheme.colorScheme.primaryContainer,
                    colorText = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            items(viewModel.listAppSetting) {
                val expandedMenu = remember {
                    mutableStateOf(false)
                }
                val result = when (it) {
                    LANG -> {
                        Pair(
                            R.string.languages_settings,
                            uiState.value.language ?: viewModel.getLanguage()
                        )
                    }

                    THEME -> {
                        Pair(
                            R.string.theme_settings, when (
                                uiState.value.theme ?: viewModel.getTheme()
                            ) {
                                TypeTheme.SYSTEM.name -> stringResource(id = R.string.theme_text_system)
                                TypeTheme.LIGHT.name -> stringResource(id = R.string.theme_text_light)
                                TypeTheme.DARK.name -> stringResource(id = R.string.theme_text_dark)
                                else -> null
                            }
                        )
                    }

                    NOTIFICATION -> {
                        val enabled = uiState.value.notification ?: viewModel.getNotification()
                        Pair(
                            R.string.notifications_settings,
                            stringResource(id = if (enabled) R.string.enabled_label else R.string.disabled_label)
                        )
                    }

                    else -> Pair(0, "")
                }
                result.second?.let { second ->
                    SettingItem(
                        title = result.first,
                        value = second,
                        onClick = { expandedMenu.value = true },
                        menu = {
                            ShowMenuSetting(
                                expanded = expandedMenu,
                                type = it,
                                onClick = viewModel::onClickItem
                            )
                        }
                    )
                }
            }
            item {
                HeaderSetting(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    id = R.string.account_label,
                    colorContainer = MaterialTheme.colorScheme.primaryContainer,
                    colorText = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            items(viewModel.listAccSetting) {
                val result = when (it) {
                    CONFIDENTIALITY -> {
                        Pair(
                            R.string.privacy_settings,
                            uiState.value.privacy ?: viewModel.getPrivacy()
                        )
                    }

                    PERS_DATA -> {
                        Pair(R.string.personal_data, "")
                    }

                    EDIT_PASSWORD -> {
                        Pair(R.string.change_password_settings, "")
                    }

                    DELETE_AN_ACCOUNT -> {
                        Pair(R.string.delete_an_account, "")
                    }

                    else -> Pair(0, "")
                }
                SettingItem(
                    title = result.first,
                    value = result.second,
                    onClick = {
                        when (result.first) {
                            R.string.change_password_settings -> {
                                viewModel.showDialog(SettingsDialogs.PASS)
                            }

                            R.string.delete_an_account -> {
                                viewModel.showDialog(SettingsDialogs.DELETE_ACCOUNT)
                            }
                        }
                    }
                )
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.eventsFlow.collectLatest { value ->
            when (value) {
                is SettingsScreenEvent.NavigateTo -> {
                    if (value.route == Constants.ONBOARD_ROUTE) {
                        navController.navigate(value.route)
                        navController.clearBackStack(value.route)
                    } else {
                        navController.navigate(value.route)
                    }
                }

                is SettingsScreenEvent.ToastEvent -> {
                    when (value.msg) {
                        ResponseStatus.FAILED.value -> {
                            Toast.makeText(
                                context,
                                R.string.error_toast, Toast.LENGTH_SHORT
                            ).show()
                        }

                        ResponseStatus.SUCCESS.value -> {
                            Toast.makeText(
                                context,
                                R.string.pass_been_updat, Toast.LENGTH_SHORT
                            ).show()
                        }

                        else -> {
                            Toast.makeText(
                                context,
                                value.msg, Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                is SettingsScreenEvent.ThemeEvent -> {
                    themeState.value = value.theme
                }

                is SettingsScreenEvent.SetLanguage -> {
                    Ext.setLanguage(context = context, language = value.language.languageCode)
                    (context as Activity).recreate()
                }
            }
        }
    }
    val lifeCycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.init()
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
private fun SettingIcon(
    @DrawableRes id: Int
) {
    Icon(
        painter = painterResource(id = id),
        contentDescription = Constants.CONTENT_DESCRIPTION,
        tint = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun HeaderSetting(
    modifier: Modifier = Modifier,
    @StringRes id: Int,
    colorContainer: Color = Color.Transparent,
    colorText: Color = MaterialTheme.colorScheme.onBackground,
) {
    Box(
        modifier = Modifier
            .background(color = colorContainer)
            .fillMaxWidth()
    ) {
        Text(
            modifier = modifier,
            text = stringResource(id = id),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            color = colorText
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingItem(
    @StringRes title: Int,
    value: String = "",
    icon: @Composable () -> Unit = { SettingIcon(id = R.drawable.ic_arrow_right) },
    menu: @Composable () -> Unit = {},
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        onClick = { onClick() },
        colors = CardDefaults.cardColors(
            containerColor = Color.Transparent
        ),
        shape = MaterialTheme.shapes.small.copy(CornerSize(0.dp))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                HeaderSetting(
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                    id = title,
                    colorText = if (title == R.string.delete_an_account)
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.onBackground
                )
                if (value.isNotEmpty()) {
                    Text(
                        modifier = Modifier.padding(horizontal = 8.dp),
                        text = value,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
            menu()
            icon()
        }
        Divider(
            modifier = Modifier.fillMaxWidth(),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
    }
}

@Composable
private fun ShowMenuSetting(
    expanded: MutableState<Boolean>,
    type: SettingType,
    onClick: (Pair<SettingType, Any>) -> Unit
) {
    when (type) {
        LANG -> {
            ShowMenu(
                expanded = expanded,
                data = Constants.listTypeLang,
                onCLick = {
                    onClick(Pair(type, it))
                }
            )
        }

        THEME -> {
            ShowMenu(
                expanded = expanded,
                data = Constants.listTypeTheme,
                onCLick = {
                    onClick(Pair(type, it))
                }
            )
        }

        NOTIFICATION -> {
            ShowMenu(
                expanded = expanded,
                data = Constants.listTypeNotifications,
                onCLick = {
                    onClick(Pair(type, it))
                }
            )
        }

        PERS_DATA -> {}
        CONFIDENTIALITY -> {}
        EDIT_PASSWORD -> {}
        DELETE_AN_ACCOUNT -> {}
    }
}