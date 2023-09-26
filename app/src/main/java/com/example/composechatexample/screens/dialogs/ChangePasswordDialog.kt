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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composechatexample.R
import com.example.composechatexample.components.ActionButton
import com.example.composechatexample.screens.settings.SettingsViewModel
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.PassUpdateState

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ChangePasswordDialog() {
    val viewModel: SettingsViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    val pattern = remember { Regex("[0-9a-zA-Z]*") }
    AlertDialog(
        modifier = Modifier
            .padding(horizontal = 15.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        title = {
            Text(
                text = stringResource(id = R.string.change_password),
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        },
        text = {
            Column() {
                OutlinedTextField(
                    value = uiState.value.pass.currentPass,
                    onValueChange = {
                        if (it.matches(pattern))
                            viewModel.changeCurrentPass(it)
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.current_pass),
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    trailingIcon = {
                        ClearFieldIcon(
                            onClick = {
                                viewModel.clearPassField("current")
                            }
                        )
                    },
                    isError = uiState.value.errors.currentField != PassUpdateState.FIELD_CORRECTLY,
                    supportingText = {
                        if (uiState.value.errors.currentField == PassUpdateState.EMPTY_FIELD)
                            Text(text = stringResource(id = R.string.empty_field_error))
                        if (uiState.value.errors.currentField == PassUpdateState.CURRENT_NOT_EQ_OLD)
                            Text(text = stringResource(id = R.string.current_not_eq_old))
                        if (uiState.value.errors.currentField == PassUpdateState.NOT_MATCH_PATTERN)
                            Text(text = stringResource(id = R.string.password_dont_match))
                        if (uiState.value.errors.currentField == PassUpdateState.NEW_CURRENT_SAME)
                            Text(text = stringResource(id = R.string.new_pass_eq_current))
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    shape = MaterialTheme.shapes.small,
                    singleLine = true,
                )
                OutlinedTextField(
                    value = uiState.value.pass.newPass,
                    onValueChange = {
                        if (it.matches(pattern))
                            viewModel.changeNewPass(it)
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.new_pass),
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    trailingIcon = {
                        ClearFieldIcon(
                            onClick = {
                                viewModel.clearPassField("new")
                            }
                        )
                    },
                    isError = uiState.value.errors.newField != PassUpdateState.FIELD_CORRECTLY,
                    supportingText = {
                        if (uiState.value.errors.newField == PassUpdateState.EMPTY_FIELD)
                            Text(text = stringResource(id = R.string.empty_field_error))
                        if (uiState.value.errors.newField == PassUpdateState.NOT_MATCH_PATTERN)
                            Text(text = stringResource(id = R.string.password_dont_match))
                        if (uiState.value.errors.newField == PassUpdateState.NEW_EQ_OLD)
                            Text(text = stringResource(id = R.string.new_pass_eq_old))
                        if (uiState.value.errors.newField == PassUpdateState.NEWEST_NOT_SAME)
                            Text(text = stringResource(id = R.string.new_password_net_match))
                        if (uiState.value.errors.newField == PassUpdateState.NEW_CURRENT_SAME)
                            Text(text = stringResource(id = R.string.new_pass_eq_current))
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    shape = MaterialTheme.shapes.small,
                    singleLine = true,
                )
                OutlinedTextField(
                    value = uiState.value.pass.repeatedPass,
                    onValueChange = {
                        if (it.matches(pattern))
                            viewModel.changeRepeatedPass(it)
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.repeat_new_pass),
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    trailingIcon = {
                        ClearFieldIcon(
                            onClick = {
                                viewModel.clearPassField("repeat")
                            }
                        )
                    },
                    isError = uiState.value.errors.repeatedField != PassUpdateState.FIELD_CORRECTLY,
                    supportingText = {
                        if (uiState.value.errors.repeatedField == PassUpdateState.EMPTY_FIELD)
                            Text(text = stringResource(id = R.string.empty_field_error))
                        if (uiState.value.errors.repeatedField == PassUpdateState.NOT_MATCH_PATTERN)
                            Text(text = stringResource(id = R.string.password_dont_match))
                        if (uiState.value.errors.repeatedField == PassUpdateState.NEWEST_NOT_SAME)
                            Text(text = stringResource(id = R.string.new_password_net_match))
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.background
                    ),
                    shape = MaterialTheme.shapes.small,
                    singleLine = true,
                )
            }
        },
        onDismissRequest = { viewModel.showDialog() },
        dismissButton = {
            ActionButton(
                text = stringResource(id = R.string.cancel_label),
                onClick = { viewModel.showDialog() }
            )
        },
        confirmButton = {
            ActionButton(
                text = stringResource(id = R.string.accept_label),
                onClick = viewModel::updatePass
            )
        })
}

@Composable
fun ClearFieldIcon(
    onClick: () -> Unit
) {
    IconButton(
        onClick = { onClick() }
    ) {
        Icon(
            painter = painterResource(R.drawable.ic_clear),
            contentDescription = Constants.CONTENT_DESCRIPTION
        )
    }
}