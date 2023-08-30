package com.example.composechatexample.screens.dialogs

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.composechatexample.R
import com.example.composechatexample.components.ActionButton
import com.example.composechatexample.screens.profile.ProfileViewModel

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun FriendAddRemoveDialog(
    addRequest: Boolean = false
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    AlertDialog(
        modifier = Modifier
            .padding(horizontal = 15.dp),
        properties = DialogProperties(usePlatformDefaultWidth = false),
        onDismissRequest = viewModel::showAddRemoveDialog,
        text = {
            Text(
                modifier = Modifier
                    .fillMaxWidth(),
                text = stringResource(
                    id = if (addRequest) {
                        R.string.add_user_to_friend
                    } else {
                        R.string.remove_friend_check_quest
                    }, uiState.value.username
                ),
                color = MaterialTheme.colorScheme.onPrimaryContainer,
                textAlign = TextAlign.Center,
                fontSize = 18.sp,
            )
        },
        confirmButton = {
            ActionButton(
                text = stringResource(id = R.string.accept_label),
                onClick = {
                    if (addRequest) viewModel.friendshipRequest()
                    else viewModel.removeFriend()
                }
            )
        },
        dismissButton = {
            ActionButton(
                text = stringResource(id = R.string.decline),
                onClick = viewModel::showAddRemoveDialog
            )
        }
    )
}