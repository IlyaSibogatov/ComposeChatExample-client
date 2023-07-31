package com.example.composechatexample.screens.settings

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
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
import com.example.composechatexample.screens.settings.model.SettingsScreenEvent
import com.example.composechatexample.screens.settings.model.SettingsUIState
import com.example.composechatexample.ui.theme.themeState
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.Ext
import com.example.composechatexample.utils.Type
import com.example.composechatexample.utils.TypeTheme
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SettingsScreen(
    navController: NavHostController,
) {
    val context = LocalContext.current
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    Column() {
        ApplicationSettings(uiState, viewModel, context)
        AccountSettings(uiState, viewModel, context)
        Spacer(
            modifier = Modifier
                .weight(1f)
        )
        SupportAndExit(uiState, viewModel, context)
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.eventsFlow.collectLatest { value ->
            when (value) {
                is SettingsScreenEvent.NavigateTo -> {
                    if (value.route == Constants.ONBOARD_ROUTE) {
                        navController.navigate(value.route) {
                            popUpTo(0)
                        }
                    } else {
                        navController.navigate(value.route)
                    }
                }

                is SettingsScreenEvent.ToastEvent -> {
                    Toast.makeText(context, value.msg, Toast.LENGTH_SHORT).show()
                }

                is SettingsScreenEvent.ThemeEvent -> {
                    themeState.value = value.theme
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

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun ApplicationSettings(
    uiState: State<SettingsUIState>,
    viewModel: SettingsViewModel,
    context: Context
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.application_label),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
    }
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { viewModel.onLanguageClick() }
                )
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.languages_settings),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = uiState.value.language,
                    fontSize = 16.sp,
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = Constants.CONTENT_DESCRIPTION
            )
        }
        val expandedMenu = remember {
            mutableStateOf(false)
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
                        expandedMenu.value = true
                    }
                )
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = stringResource(id = R.string.theme_settings),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = when (uiState.value.theme) {
                        TypeTheme.SYSTEM.name -> stringResource(id = R.string.theme_text_system)
                        TypeTheme.LIGHT.name -> stringResource(id = R.string.theme_text_light)
                        else -> stringResource(id = R.string.theme_text_dark)
                    },
                    fontSize = 16.sp,
                )
                ShowMenu(
                    expanded = expandedMenu,
                    data = listOf(
                        Type(nameType = TypeTheme.SYSTEM, str = R.string.theme_text_system),
                        Type(nameType = TypeTheme.DARK, str = R.string.theme_text_dark),
                        Type(nameType = TypeTheme.LIGHT, str = R.string.theme_text_light),
                    ),
                    onCLick = {
                        viewModel.saveTheme(it)
                    }
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = Constants.CONTENT_DESCRIPTION
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
                        Ext.showToast(
                            context = context,
                            context.resources.getString(R.string.development)
                        )
                    }
                )
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column() {
                Text(
                    text = stringResource(id = R.string.notifications_settings),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = uiState.value.notification,
                    fontSize = 16.sp,
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = Constants.CONTENT_DESCRIPTION
            )
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun AccountSettings(
    uiState: State<SettingsUIState>,
    viewModel: SettingsViewModel,
    context: Context
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.LightGray)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(id = R.string.account_label),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )
    }
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
                        Ext.showToast(
                            context = context,
                            context.resources.getString(R.string.development)
                        )
                    }
                )
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column() {
                Text(
                    text = stringResource(id = R.string.privacy_settings),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = uiState.value.privacy,
                    fontSize = 16.sp,
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = Constants.CONTENT_DESCRIPTION
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = { }
                )
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.personal_data),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = Constants.CONTENT_DESCRIPTION
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
                        Ext.showToast(
                            context = context,
                            context.resources.getString(R.string.development)
                        )
                    }
                )
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.change_password_settings),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = Constants.CONTENT_DESCRIPTION
            )
        }
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@Composable
fun SupportAndExit(
    uiState: State<SettingsUIState>,
    viewModel: SettingsViewModel,
    context: Context
) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
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
                )
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column() {
                Text(
                    text = stringResource(id = R.string.tech_support_label),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = uiState.value.support,
                    fontSize = 16.sp,
                )
            }
            Image(
                painter = painterResource(id = R.drawable.ic_arrow_right),
                contentDescription = Constants.CONTENT_DESCRIPTION
            )

        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = viewModel::userLogOut
                )
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = stringResource(id = R.string.exit_label),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
            )
            Image(
                painter = painterResource(id = R.drawable.ic_logout),
                contentDescription = Constants.CONTENT_DESCRIPTION
            )
        }
    }
}