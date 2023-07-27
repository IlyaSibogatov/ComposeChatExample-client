package com.example.composechatexample.screens.chat.chatlist

import androidx.compose.material3.AlertDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun EditChatDialog() {
    val viewModel: ChatListViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

//    AlertDialog(
//        onDismissRequest = {
//        viewModel.showEditChatDialog(false)
//    }) {
//
//    }
}