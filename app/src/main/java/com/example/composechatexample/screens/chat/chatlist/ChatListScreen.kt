package com.example.composechatexample.screens.chat.chatlist

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.example.composechatexample.R
import com.example.composechatexample.screens.chat.chatlist.model.ChatListScreenEvent
import com.example.composechatexample.utils.CircularLoader
import com.example.composechatexample.utils.Constants
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
fun ChatListScreen(
    navController: NavHostController,
) {
    val context = LocalContext.current
    val viewModel: ChatListViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    if (!uiState.value.userLogged)
        navController.navigate(Constants.ONBOARD_ROUTE)
    else {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .onGloballyPositioned {
                },
        ) {
            val (
                lcChats, searchRow, createChatBtn, chatName, chatOwner, createDate, privateIcon,
            ) = createRefs()
            when {
                uiState.value.dialogs.passDialog -> EntryDialog()
                uiState.value.dialogs.createDialog -> CreateChatDialog()
                else -> {}
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(color = Color.LightGray)
                    .padding(15.dp)
                    .constrainAs(searchRow) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                horizontalArrangement = Arrangement.Center
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .background(Color.Transparent)
                        .fillMaxWidth(),
                    value = uiState.value.searchQuery,
                    onValueChange = { newText ->
                        viewModel.updateSearchQuery(newText)
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.find_chat),
                            color = Color.LightGray
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = Color.White,
                        placeholderColor = Color.White,
                    ),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { keyboardController?.hide() }),
                )
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(lcChats) {
                        top.linkTo(searchRow.bottom)
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                    },
            ) {
                items(uiState.value.newChats) { item ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp)
                            .background(Color.Transparent),
                        shape = MaterialTheme.shapes.small,
                        onClick = {
                            viewModel.checkChat(item)
                        }
                    ) {
                        ConstraintLayout(
                            modifier = Modifier
                                .padding(16.dp)
                                .fillMaxWidth()
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                                    .constrainAs(chatName) {
                                        start.linkTo(parent.start)
                                        top.linkTo(parent.top)
                                        end.linkTo(privateIcon.start)
                                        width = Dimension.fillToConstraints
                                    },
                                text = item.name,
                                textAlign = TextAlign.Start,
                                fontSize = 22.sp,
                                color = Color.Black,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                modifier = Modifier
                                    .padding(bottom = 16.dp)
                                    .constrainAs(chatOwner) {
                                        start.linkTo(parent.start)
                                        end.linkTo(chatName.end)
                                        top.linkTo(chatName.bottom)
                                        width = Dimension.fillToConstraints
                                    },
                                text = stringResource(id = R.string.owner_label, item.owner),
                                textAlign = TextAlign.Start,
                                fontSize = 22.sp,
                                color = Color.Black,
                            )
                            Text(
                                modifier = Modifier
                                    .constrainAs(createDate) {
                                        start.linkTo(parent.start)
                                        end.linkTo(chatName.end)
                                        top.linkTo(chatOwner.bottom)
                                        width = Dimension.fillToConstraints
                                    },
                                text = stringResource(id = R.string.date_label, item.formattedTime),
                                textAlign = TextAlign.Start,
                                fontSize = 18.sp,
                                color = Color.Black,
                                fontStyle = FontStyle.Italic
                            )
                            Icon(
                                modifier = Modifier
                                    .constrainAs(privateIcon) {
                                        top.linkTo(chatName.top)
                                        end.linkTo(parent.end)
                                    },
                                painter = painterResource(
                                    id =
                                    if (item.password.isBlank()) R.drawable.ic_not_a_private
                                    else R.drawable.ic_private
                                ),
                                contentDescription = Constants.CONTENT_DESCRIPTION,
                            )
                        }
                    }
                }
            }
            OutlinedButton(
                modifier = Modifier
                    .padding(8.dp)
                    .constrainAs(createChatBtn) {
                        bottom.linkTo(parent.bottom)
                        end.linkTo(parent.end)
                    },
                shape = CircleShape,
                onClick = viewModel::showCreateDialog,
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_new_chat),
                    contentDescription = Constants.CONTENT_DESCRIPTION
                )
            }
        }
        if (uiState.value.isLoading) CircularLoader()
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.eventsFlow.collectLatest { value ->
            when (value) {
                is ChatListScreenEvent.NavigateTo -> navController.navigate(value.route)
                is ChatListScreenEvent.ToastEvent -> {
                    Toast.makeText(context, value.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    val lifeCycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) viewModel.loadChatList()
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}