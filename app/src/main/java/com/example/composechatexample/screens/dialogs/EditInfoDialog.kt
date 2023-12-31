package com.example.composechatexample.screens.dialogs

import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.remember
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
import com.example.composechatexample.screens.profile.ProfileViewModel
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.Constants.EMPTY_VALUE

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun EditInfoDialog() {
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    AlertDialog(
        modifier = Modifier
            .padding(horizontal = 15.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        title = {
            Text(
                text = stringResource(id = R.string.user_information)
            )
        },
        onDismissRequest = { viewModel.showEditDialog() },
        text = {
            Column() {
                val pattern = remember { Regex("[0-9a-zA-Z]*") }
                OutlinedTextField(
                    value = uiState.value.newInfo.username,
                    onValueChange = {
                        if (it.matches(pattern)) viewModel.updateNameValue(it)
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.nickname),
                            color = Color.LightGray
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (uiState.value.newInfo.username.isNotBlank())
                                    viewModel.updateNameValue(EMPTY_VALUE)
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_clear),
                                contentDescription = Constants.CONTENT_DESCRIPTION
                            )
                        }
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    shape = MaterialTheme.shapes.small,
                    isError =
                    uiState.value.errors.emptyUsername ||
                            uiState.value.errors.newInfoNotChanged ||
                            uiState.value.errors.userNameNotMatched,
                    supportingText = {
                        if (uiState.value.errors.userNameNotMatched)
                            Text(
                                text = stringResource(id = R.string.check_username)
                            )
                        if (uiState.value.errors.emptyUsername)
                            Text(
                                text = stringResource(id = R.string.username_empty)
                            )
                        if (uiState.value.errors.newInfoNotChanged)
                            Text(
                                text = stringResource(id = R.string.info_dont_changes)
                            )
                    },
                    singleLine = true,
                )
                OutlinedTextField(
                    value = uiState.value.newInfo.selfInfo,
                    onValueChange = viewModel::updateInfoValue,
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.user_info_label),
                            color = Color.LightGray
                        )
                    },
                    trailingIcon = {
                        IconButton(
                            onClick = {
                                if (uiState.value.newInfo.selfInfo.isNotBlank())
                                    viewModel.updateInfoValue(EMPTY_VALUE)
                            }
                        ) {
                            Icon(
                                painter = painterResource(R.drawable.ic_clear),
                                contentDescription = Constants.CONTENT_DESCRIPTION
                            )
                        }
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    shape = MaterialTheme.shapes.small,
                    isError = uiState.value.errors.newInfoNotChanged,
                    supportingText = {
                        if (uiState.value.errors.newInfoNotChanged)
                            Text(
                                text = stringResource(id = R.string.info_dont_changes)
                            )
                    },
                    maxLines = 5,
                )
            }
        },
        confirmButton = {
            ActionButton(
                text = stringResource(id = R.string.accept_label),
                onClick = {
                    viewModel.updateInfo()
                }
            )
        },
        dismissButton = {
            ActionButton(
                text = stringResource(id = R.string.cancel_label),
                onClick = {
                    viewModel.showEditDialog()
                }
            )
        },
    )
}