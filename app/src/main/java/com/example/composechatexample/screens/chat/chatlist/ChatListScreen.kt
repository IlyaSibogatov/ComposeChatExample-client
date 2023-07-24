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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
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
import androidx.compose.ui.Alignment
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.example.composechatexample.R
import com.example.composechatexample.domain.model.Chat
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
            val (lcChats, searchRow, createChatBtn ) = createRefs()
            when {
                uiState.value.dialogs.passDialog -> EntryDialog()
                uiState.value.dialogs.createDialog -> CreateChatDialog()
                else -> {}
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primary)
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
                        .fillMaxWidth(),
                    value = uiState.value.searchQuery,
                    onValueChange = { newText ->
                        viewModel.updateSearchQuery(newText)
                    },
                    placeholder = {
                        Text(
                            text = stringResource(id = R.string.find_chat),
                            style = MaterialTheme.typography.labelLarge
                        )
                    },
                    colors = TextFieldDefaults.outlinedTextFieldColors(
                        containerColor = MaterialTheme.colorScheme.background
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
                    ItemChat(
                        data = item,
                        modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                        onChatClick = {
                            viewModel.checkChat(item)
                        }
                    )
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemChat(
    data: Chat,
    onChatClick: () -> Unit,
    modifier: Modifier
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = MaterialTheme.shapes.small,
        onClick = { onChatClick() },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Row(
            modifier = modifier.padding(top = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            TextChatItem(
                text = data.name,
                modifier = Modifier.weight(1f),
                size = 18.sp,
                weight = FontWeight.Bold
            )
            Icon(
                painter = painterResource(
                    id = if (data.password.isEmpty()) R.drawable.ic_not_a_private
                    else R.drawable.ic_private
                ),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                contentDescription = Constants.CONTENT_DESCRIPTION
            )
        }
        TextChatItem(
            modifier = modifier.padding(bottom = 4.dp),
            text = stringResource(id = R.string.owner_label, data.owner),
            size = 16.sp
        )
        Divider(
            modifier = modifier,
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outline
        )
        TextChatItem(
            modifier = modifier.padding(bottom = 8.dp),
            text = stringResource(id = R.string.date_label, data.formattedTime),
            style = FontStyle.Italic,
            size = 10.sp
        )
    }
}

@Composable
private fun TextChatItem(
    text: String,
    modifier: Modifier,
    size: TextUnit,
    style: FontStyle = FontStyle.Normal,
    weight: FontWeight = FontWeight.Normal
){
    Text(
        modifier = modifier,
        text = text,
        textAlign = TextAlign.Start,
        fontSize = size,
        color = MaterialTheme.colorScheme.onPrimaryContainer,
        fontStyle = style,
        fontWeight = weight
    )
}

@Preview
@Composable
fun PreviewCardChat() {
    ItemChat(data = Chat(
        name = "Test",
        password = "",
        owner = "Person",
        formattedTime = "24.07.2023 19 12",
        id = "ibdifbaibfa"
    ),
        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
        onChatClick = { /*TODO*/ }
    )
}