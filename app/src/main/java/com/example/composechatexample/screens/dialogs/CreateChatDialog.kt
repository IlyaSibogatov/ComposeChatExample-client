package com.example.composechatexample.screens.dialogs

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composechatexample.R
import com.example.composechatexample.components.ActionButton
import com.example.composechatexample.components.CustomIconButton
import com.example.composechatexample.screens.chat.chatlist.ChatListViewModel

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun CreateChatDialog() {
    val viewModel: ChatListViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()
    val interactionSource = remember {
        MutableInteractionSource()
    }

    AlertDialog(
        modifier = Modifier
            .padding(horizontal = 15.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = viewModel::showCreateDialog,
        title = {
            Text(
                text =
                if (!uiState.value.createdChat.passEnable) stringResource(id = R.string.public_chat_lable)
                else (stringResource(id = R.string.private_chat_lable)),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        text = {
            ConstraintLayout() {
                val (chatName, chatPass, passCheckBox) = createRefs()
                OutlinedTextField(
                    modifier = Modifier
                        .constrainAs(chatName) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        },
                    value = uiState.value.createdChat.chatName,
                    onValueChange = viewModel::updateOwnChatName,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.enter_room_name_label),
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    trailingIcon = {
                        CustomIconButton(
                            imageId = R.drawable.ic_clear,
                            color = when {
                                interactionSource.collectIsFocusedAsState().value -> MaterialTheme.colorScheme.onSurfaceVariant
                                else -> MaterialTheme.colorScheme.onSurfaceVariant
                            },
                            onClick = {
                                if (uiState.value.createdChat.chatName.isNotBlank())
                                    viewModel.updateOwnChatName("")
                            }
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    interactionSource = interactionSource,
                    shape = MaterialTheme.shapes.small,
                    isError = uiState.value.errors.emptyChatName,
                    supportingText = {
                        if (uiState.value.errors.emptyChatName)
                            Text(text = stringResource(id = R.string.empty_chat_name))
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
                                text = stringResource(id = R.string.enter_password),
                                style = MaterialTheme.typography.labelLarge
                            )
                        },
                        trailingIcon = {
                            CustomIconButton(
                                imageId = R.drawable.ic_clear,
                                color = when {
                                    interactionSource.collectIsFocusedAsState().value -> MaterialTheme.colorScheme.onSurfaceVariant
                                    else -> MaterialTheme.colorScheme.onSurfaceVariant
                                },
                                onClick = {
                                    if (uiState.value.createdChat.chatPass.isNotBlank())
                                        viewModel.updateOwnChatPass("")
                                }
                            )
                        },
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            containerColor = MaterialTheme.colorScheme.background
                        ),
                        isError = uiState.value.errors.emptyChatPass,
                        supportingText = {
                            if (uiState.value.errors.emptyChatPass)
                                Text(text = stringResource(id = R.string.fill_password))
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
            ActionButton(
                text = stringResource(id = R.string.create_label),
                onClick = viewModel::createChat
            )
        },
        dismissButton = {
            ActionButton(
                text = stringResource(id = R.string.cancel_label),
                onClick = viewModel::showCreateDialog
            )
        }
    )
}