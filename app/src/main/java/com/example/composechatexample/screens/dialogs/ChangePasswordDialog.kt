package com.example.composechatexample.screens.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import com.example.composechatexample.screens.settings.SettingsViewModel
import com.example.composechatexample.utils.Constants

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
                            text = stringResource(id = R.string.current_pass)
                        )
                    },
                    trailingIcon = {
                        ClearFieldIcon(
                            onClick = {
                                viewModel.clearPassField("current")
                            }
                        )
                    },
                    isError = with(uiState.value.errors) {
                        this.currentIsEmpty ||
                                this.currentNotEqOld ||
                                this.currentNotMatch ||
                                this.currentNewSame
                    },
                    supportingText = {
                        if (uiState.value.errors.currentIsEmpty)
                            Text(text = stringResource(id = R.string.empty_field_error))
                        if (uiState.value.errors.currentNotEqOld)
                            Text(text = stringResource(id = R.string.current_not_eq_old))
                        if (uiState.value.errors.currentNotMatch)
                            Text(text = stringResource(id = R.string.password_dont_match))
                        if (uiState.value.errors.currentNewSame)
                            Text(text = stringResource(id = R.string.new_pass_eq_current))
                    },
                )
                OutlinedTextField(
                    value = uiState.value.pass.newPass,
                    onValueChange = {
                        if (it.matches(pattern))
                            viewModel.changeNewPass(it)
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.new_pass)
                        )
                    },
                    trailingIcon = {
                        ClearFieldIcon(
                            onClick = {
                                viewModel.clearPassField("new")
                            }
                        )
                    },
                    isError = with(uiState.value.errors) {
                        this.newIsEmpty ||
                                this.newEqOld ||
                                this.newestNotSame ||
                                this.newNotMatch ||
                                this.currentNewSame
                    },
                    supportingText = {
                        if (uiState.value.errors.newIsEmpty)
                            Text(text = stringResource(id = R.string.empty_field_error))
                        if (uiState.value.errors.newEqOld)
                            Text(text = stringResource(id = R.string.new_pass_eq_old))
                        if (uiState.value.errors.newestNotSame)
                            Text(text = stringResource(id = R.string.new_password_net_match))
                        if (uiState.value.errors.newNotMatch)
                            Text(text = stringResource(id = R.string.password_dont_match))
                        if (uiState.value.errors.currentNewSame)
                            Text(text = stringResource(id = R.string.new_pass_eq_current))
                    },
                )
                OutlinedTextField(
                    value = uiState.value.pass.repeatedPass,
                    onValueChange = {
                        if (it.matches(pattern))
                            viewModel.changeRepeatedPass(it)
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.repeat_new_pass)
                        )
                    },
                    trailingIcon = {
                        ClearFieldIcon(
                            onClick = {
                                viewModel.clearPassField("repeat")
                            }
                        )
                    },
                    isError = with(uiState.value.errors) {
                        this.repeatIsEmpty ||
                                this.newestNotSame ||
                                this.repeatNotMatch
                    },
                    supportingText = {
                        if (uiState.value.errors.repeatIsEmpty)
                            Text(text = stringResource(id = R.string.empty_field_error))
                        if (uiState.value.errors.repeatNotMatch)
                            Text(text = stringResource(id = R.string.password_dont_match))
                        if (uiState.value.errors.newestNotSame)
                            Text(text = stringResource(id = R.string.new_password_net_match))
                    },
                )
            }
        },
        onDismissRequest = viewModel::showChangePassDialog,
        dismissButton = {
            Button(
                onClick = viewModel::showChangePassDialog
            ) {
                Text(
                    stringResource(id = R.string.accept_label)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = viewModel::updatePass
            ) {
                Text(
                    stringResource(id = R.string.accept_label)
                )
            }
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