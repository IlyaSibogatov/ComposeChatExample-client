package com.example.composechatexample.screens.chat.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composechatexample.R
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
        title = { Text(text = "Chat is private") },
        text = {
            Column() {
                Text(
                    modifier = Modifier.padding(PaddingValues(bottom = 8.dp)),
                    text = "Enter your password, if you don't know it, ask the owner of the chat room."
                )
                OutlinedTextField(
                    modifier = Modifier
                        .background(Color.Transparent),
                    value = uiState.value.roomPassword,
                    onValueChange = viewModel::updatePassword,
                    placeholder = {
                        Text(
                            text = "Enter password",
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
                            Text(text = "Incorrect password entered")
                    },
                    singleLine = true,
                )
            }
        },
        confirmButton = {
            Button(
                onClick = viewModel::checkPassword
            ) { Text("Accept") }
        },
        dismissButton = {
            Button(
                onClick = { viewModel.showAlertDialog(null) }
            ) { Text("Cancel") }
        }
    )
}