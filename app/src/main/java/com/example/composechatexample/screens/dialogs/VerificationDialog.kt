package com.example.composechatexample.screens.dialogs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composechatexample.R
import com.example.composechatexample.components.ActionButton
import com.example.composechatexample.screens.settings.SettingsViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun VerificationDialog(
    title: String? = null,
    text: String? = null,
    acceptOnClick: () -> Unit,
    declinedOnClick: () -> Unit,
) {

    val viewModel: SettingsViewModel = hiltViewModel()

    AlertDialog(
        modifier = Modifier
            .padding(horizontal = 15.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = { viewModel.showDialog() },
        text = {
            Column() {
                text?.let {
                    Text(
                        modifier = Modifier,
                        text = it,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        },
        confirmButton = {
            ActionButton(
                text = stringResource(id = R.string.accept_label),
                onClick = { acceptOnClick() }
            )
        },
        dismissButton = {
            ActionButton(
                text = stringResource(id = R.string.cancel_label),
                onClick = { declinedOnClick() }
            )
        }
    )
}