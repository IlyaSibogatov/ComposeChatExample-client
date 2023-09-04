package com.example.composechatexample.screens.dialogs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composechatexample.R
import com.example.composechatexample.components.ActionButton
import com.example.composechatexample.screens.chat.chatlist.ChatListViewModel
import com.example.composechatexample.utils.Constants

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun EntryDialog() {
    val viewModel: ChatListViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    AlertDialog(
        modifier = Modifier
            .padding(horizontal = 15.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { viewModel.showAlertDialog(null) },
        title = { Text(text = stringResource(id = R.string.private_chat_lable)) },
        text = {
            Column() {
                Text(
                    modifier = Modifier.padding(PaddingValues(bottom = 8.dp)),
                    text = stringResource(id = R.string.fill_password_for_enter)
                )
                OutlinedTextField(
                    modifier = Modifier
                        .background(Color.Transparent),
                    value = uiState.value.roomPassword,
                    onValueChange = viewModel::updatePassword,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.enter_password),
                            color = Color.LightGray
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (uiState.value.roomPassword.isNotBlank())
                                    viewModel.updatePassword("")
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_clear),
                                contentDescription = Constants.CONTENT_DESCRIPTION
                            )
                        }
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        placeholderColor = Color.White,
                    ),
                    shape = MaterialTheme.shapes.small,
                    isError = uiState.value.errors.entryPasswordError,
                    supportingText = {
                        if (uiState.value.errors.entryPasswordError)
                            Text(text = stringResource(id = R.string.incorrect_password_label))
                    },
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            ActionButton(
                text = stringResource(id = R.string.accept_label),
                onClick = viewModel::checkPassword
            )
        },
        dismissButton = {
            ActionButton(
                text = stringResource(id = R.string.cancel_label),
                onClick = { viewModel.showAlertDialog(null) }
            )
        }
    )
}