package com.example.composechatexample.screens.onboarding

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.example.composechatexample.R
import com.example.composechatexample.components.ActionButton
import com.example.composechatexample.screens.onboarding.model.OnBoardingUIState
import com.example.composechatexample.screens.onboarding.model.OnboardScreenEvent
import com.example.composechatexample.utils.Constants
import kotlinx.coroutines.flow.collectLatest

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
        SignUpField(
            value = uiState.value.username,
            text = stringResource(id = R.string.username_hint),
            isError = uiState.value.errors.usernameError,
            checkError = { CheckUserName(uiState = uiState) },
            onValueChange = viewModel::updateUsername
        )
        SignUpField(
            value = uiState.value.password,
            text = stringResource(id = R.string.password_hint),
            isError = uiState.value.errors.passwordError,
            checkError = { CheckPassword(uiState = uiState) },
            onValueChange = viewModel::updatePassword
        )
        ActionButton(
            text = stringResource(id = if (!uiState.value.showSignUp) R.string.login_label else R.string.signup_label),
            onClick = {  if (!uiState.value.showSignUp) viewModel.login() else viewModel.signup() }
        )
        if (!uiState.value.showSignUp) {
            Text(
                text = stringResource(id = R.string.havent_account),
                style = MaterialTheme.typography.labelLarge
            )
            Text(
                modifier = Modifier
                    .clickable(
                        onClick = viewModel::changeState
                    ),
                text = stringResource(id = R.string.signup_label),
                style = MaterialTheme.typography.bodyMedium
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignUpField(
    value: String,
    text: String,
    isError: Boolean,
    checkError: @Composable () -> Unit,
    onValueChange: (String) -> Unit
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        value = value,
        onValueChange = onValueChange,
        label = { Text(text = text) },
        shape = MaterialTheme.shapes.small,
        isError = isError,
        supportingText = { checkError() },
        singleLine = true,
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = MaterialTheme.colorScheme.onPrimaryContainer ,
            focusedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}

@Composable
fun CheckUserName(uiState: State<OnBoardingUIState>) {
    if (uiState.value.errors.usernameError) {
        ErrorText(text = stringResource(id = R.string.check_username))
    } else if (uiState.value.errors.usernameEmptyError) {
        ErrorText(text = stringResource(id = R.string.username_cant_be_empty))
    }
}

@Composable
fun CheckPassword(uiState: State<OnBoardingUIState>) {
    if (uiState.value.errors.passwordError) {
        ErrorText(text = stringResource(id = R.string.check_password))
    } else if (uiState.value.errors.passwordEmptyError) {
        ErrorText(text = stringResource(id = R.string.password_cant_be_empty))
    }
}

@Composable
fun ErrorText(text: String) {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = text,
        color = MaterialTheme.colorScheme.error
    )
}