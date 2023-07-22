package com.example.composechatexample.screens.chat.chatlist

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
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
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composechatexample.R
import com.example.composechatexample.utils.Constants

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CreateChatDialog() {
    val viewModel: ChatListViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    AlertDialog(
        modifier = Modifier
            .padding(horizontal = 15.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = viewModel::showCreateDialog,
        title = {
            Text(
                text =
                if (!uiState.value.createdChat.passEnable) "Public chat"
                else ("Private chat")
            )
        },
        text = {
            ConstraintLayout() {
                val (chatName, chatPass, passCheckBox, passRow) = createRefs()
                OutlinedTextField(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .constrainAs(chatName) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    value = uiState.value.createdChat.chatName,
                    onValueChange = viewModel::updateOwnChatName,
                    placeholder = {
                        Text(
                            text = "Enter room name",
                            color = Color.LightGray
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (uiState.value.createdChat.chatName.isNotBlank())
                                    viewModel.updateOwnChatName("")
                            }
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_clear),
                                contentDescription = Constants.CONTENT_DESCRIPTION
                            )
                        }
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        placeholderColor = Color.White,
                    ),
                    shape = MaterialTheme.shapes.small,
                    isError = uiState.value.errors.emptyChatName,
                    supportingText = {
                        if (uiState.value.errors.emptyChatName)
                            Text(text = "Chat name can't be blank")
                    },
                    singleLine = true,
                )
                if (uiState.value.createdChat.passEnable) {
                    OutlinedTextField(
                        modifier = Modifier
                            .wrapContentWidth()
                            .constrainAs(chatPass) {
                                start.linkTo(chatName.start)
                                end.linkTo(chatName.end)
                                top.linkTo(passCheckBox.bottom)
                            },
                        value = uiState.value.createdChat.chatPass,
                        onValueChange = viewModel::updateOwnChatPass,
                        placeholder = {
                            Text(
                                text = "Chat password",
                                color = Color.LightGray
                            )
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = {
                                    if (uiState.value.createdChat.chatPass.isNotBlank())
                                        viewModel.updateOwnChatPass("")
                                }
                            ) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_clear),
                                    contentDescription = Constants.CONTENT_DESCRIPTION
                                )
                            }
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = Color.White,
                            placeholderColor = Color.White,
                        ),
                        isError = uiState.value.errors.emptyChatPass,
                        supportingText = {
                            if (uiState.value.errors.emptyChatPass)
                                Text(text = "Fill out the password")
                        },
                        shape = MaterialTheme.shapes.small,
                        singleLine = true,
                    )
                }
                Checkbox(
                    modifier = Modifier
                        .constrainAs(passCheckBox) {
                            end.linkTo(parent.end)
                            top.linkTo(chatName.bottom)
                        },
                    checked = uiState.value.createdChat.passEnable,
                    onCheckedChange = { viewModel.switchPass() },
                    colors = CheckboxDefaults.colors(
                        checkmarkColor = Color.Black,
                        checkedColor = Color.White,
                    )
                )
            }
        },
        confirmButton = {
            Button(
                onClick = viewModel::createChat
            ) { Text("Create") }
        },
        dismissButton = {
            Button(
                onClick = viewModel::showCreateDialog
            ) { Text("Cancel") }
        }
    )
}