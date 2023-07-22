package com.example.composechatexample.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.composechatexample.R
import com.example.composechatexample.screens.onboarding.model.OnboardScreenEvent
import com.example.composechatexample.utils.Constants
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnBoardingScreen(
    navController: NavHostController,
) {
    val viewModel: OnBoardingViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    BackHandler() {
        if (uiState.value.showSignUp) {
            viewModel.changeState()
        }
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = uiState.value.username,
            onValueChange = { newText ->
                viewModel.updateUsername(newText)
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                disabledIndicatorColor = Color.Transparent,
            ),
            label = { Text(stringResource(id = R.string.username_hint)) },
            shape = MaterialTheme.shapes.small,
            isError = uiState.value.errors.usernameError,
            supportingText = {
                if (uiState.value.errors.usernameError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.check_username),
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (uiState.value.errors.usernameEmptyError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.username_cant_be_empty),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            value = uiState.value.password,
            onValueChange = { newText ->
                viewModel.updatePassword(newText)
            },
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.White,
                disabledIndicatorColor = Color.Transparent,
            ),
            label = { Text(stringResource(id = R.string.password_hint)) },
            shape = MaterialTheme.shapes.small,
            isError = uiState.value.errors.passwordError,
            supportingText = {
                if (uiState.value.errors.passwordError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.check_password),
                        color = MaterialTheme.colorScheme.error
                    )
                } else if (uiState.value.errors.passwordEmptyError) {
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.password_cant_be_empty),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            singleLine = true,
        )
        Button(
            onClick = {
                if (!uiState.value.showSignUp) viewModel.login() else viewModel.signup()
            }
        ) {
            Text(text = stringResource(id = if (!uiState.value.showSignUp) R.string.login_label else R.string.signup_label))
        }
        if (!uiState.value.showSignUp) {
            Text(
                text = stringResource(id = R.string.havent_account)
            )
            Text(
                modifier = Modifier
                    .clickable(
                        onClick = viewModel::changeState
                    ),
                text = stringResource(id = R.string.signup_label),
                color = Color.Gray
            )
        }
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.eventsFlow.collectLatest { value ->
            when {
                (value is OnboardScreenEvent.NavigateTo && value.route == Constants.CHAT_LIST_ROUTE) -> {
                    navController.navigate(value.route) {
                        popUpTo(0)
                    }
                }
            }
        }
    }
}