package com.example.composechatexample.screens.chat.chatlist

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.SoftwareKeyboardController
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
import com.example.composechatexample.components.CircularLoader
import com.example.composechatexample.components.CustomIconButton
import com.example.composechatexample.components.ShowMenu
import com.example.composechatexample.domain.model.Chat
import com.example.composechatexample.screens.chat.chatlist.model.ChatListScreenEvent
import com.example.composechatexample.screens.dialogs.CreateChatDialog
import com.example.composechatexample.screens.dialogs.EntryDialog
import com.example.composechatexample.utils.ChatEvent
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.ResponseStatus
import com.example.composechatexample.utils.ScreenState
import com.example.composechatexample.utils.Type
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun ChatListScreen(
    navController: NavHostController,
) {
    val context = LocalContext.current
    val viewModel: ChatListViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()
    val keyboardController = LocalSoftwareKeyboardController.current

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .onGloballyPositioned { },
    ) {
        val (lcChats, searchRow, createChatBtn, errorView) = createRefs()
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
            horizontalArrangement = Arrangement.End
        ) {
            val animState = remember {
                mutableStateOf(false)
            }
            val imSearchVisible = remember {
                mutableStateOf(true)
            }
            val focusRequester = remember { FocusRequester() }
            if (imSearchVisible.value) {
                CustomIconButton(
                    imageId = R.drawable.im_search,
                    onClick = {
                        animState.value = !animState.value
                        imSearchVisible.value = !imSearchVisible.value
                    }
                )
            }
            SearchField(
                modifier = Modifier.focusRequester(focusRequester),
                animateState = animState.value,
                value = uiState.value.searchQuery,
                keyboardController = keyboardController,
                onValueChange = viewModel::updateSearchQuery,
                imVisible = {
                    imSearchVisible.value = it
                    animState.value = !it
                }
            )
        }
        when (uiState.value.screenState) {
            ScreenState.INIT -> CircularLoader()
            ScreenState.SUCCESS -> {
                when {
                    uiState.value.dialogs.passDialog -> EntryDialog()
                    uiState.value.dialogs.createDialog -> CreateChatDialog()
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
                        val expandedMenu = remember { mutableStateOf(false) }
                        ItemChat(
                            data = item,
                            modifier = Modifier.padding(start = 8.dp, end = 8.dp),
                            onChatClick = {
                                viewModel.checkChat(item)
                            },
                            onChatLongClick = {
                                viewModel.chatLongClick(item)?.let {
                                    expandedMenu.value = !expandedMenu.value
                                }
                            },
                            menu = {
                                ShowMenu(
                                    expanded = expandedMenu,
                                    data = listOf(
                                        Type(nameType = ChatEvent.EDIT, str = R.string.edit),
                                        Type(
                                            nameType = ChatEvent.REMOVE,
                                            str = R.string.remove
                                        ),
                                    ),
                                    onCLick = { type ->
                                        when (type) {
                                            ChatEvent.EDIT -> {
                                                viewModel.showCreateDialog(true)
                                                expandedMenu.value = false
                                            }

                                            ChatEvent.REMOVE -> {
                                                viewModel.deleteChat()
                                                expandedMenu.value = !expandedMenu.value
                                            }
                                        }
                                    }
                                )
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
                        contentDescription = Constants.CONTENT_DESCRIPTION,
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            ScreenState.ERROR -> {
                Column(
                    modifier = Modifier
                        .constrainAs(errorView) {
                            top.linkTo(searchRow.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        }
                ) {
                    Icon(
                        modifier = Modifier
                            .fillMaxWidth(),
                        painter = painterResource(id = R.drawable.ic_alert),
                        contentDescription = Constants.CONTENT_DESCRIPTION,
                    )
                    Text(
                        modifier = Modifier.fillMaxWidth(),
                        text = stringResource(id = R.string.try_again_later),
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {}
        }
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.eventsFlow.collectLatest { value ->
            when (value) {
                is ChatListScreenEvent.NavigateTo -> navController.navigate(value.route)
                is ChatListScreenEvent.ToastEvent -> {
                    Toast.makeText(
                        context,
                        when (value.msg) {
                            ResponseStatus.ERROR.value -> context.resources.getString(R.string.error_toast)
                            else -> value.msg
                        },
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
    val lifeCycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) {
                viewModel.loadChatList()
                viewModel.updateToken()
            }
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchField(
    modifier: Modifier = Modifier,
    animateState: Boolean,
    value: String,
    keyboardController: SoftwareKeyboardController?,
    imVisible: (Boolean) -> Unit,
    onValueChange: (String) -> Unit,
) {
    AnimatedVisibility(
        visible = animateState,
        enter = expandHorizontally(
            expandFrom = Alignment.End
        ),
        exit = shrinkHorizontally(
            shrinkTowards = Alignment.Start
        )
    ) {
        OutlinedTextField(
            modifier = modifier
                .fillMaxWidth(),
            value = value,
            onValueChange = { onValueChange(it) },
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
            keyboardActions = KeyboardActions(onDone = {
                keyboardController?.hide()
                imVisible(true)
            }),
            trailingIcon = {
                CustomIconButton(
                    imageId = R.drawable.ic_clear,
                    onClick = {
                        if (value.isNotEmpty())
                            onValueChange("")
                    }
                )
            },
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ItemChat(
    data: Chat,
    onChatClick: () -> Unit,
    onChatLongClick: () -> Unit,
    modifier: Modifier,
    menu: @Composable () -> Unit,
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, start = 16.dp, end = 16.dp)
            .combinedClickable(
                onClick = { onChatClick() },
                onLongClick = { onChatLongClick() },
            ),
        shape = MaterialTheme.shapes.small,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
    ) {
        Row(
            modifier = modifier.padding(top = 16.dp),
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
                    id = if (data.password.isBlank()) R.drawable.ic_not_a_private
                    else R.drawable.ic_private
                ),
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                contentDescription = Constants.CONTENT_DESCRIPTION
            )
            menu()
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
) {
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
        id = "ibdifbaibfa",
        ownerId = "",
    ),
        modifier = Modifier.padding(start = 16.dp, end = 16.dp),
        onChatClick = { /*TODO*/ },
        onChatLongClick = { /*TODO*/ },
        menu = { }
    )
}