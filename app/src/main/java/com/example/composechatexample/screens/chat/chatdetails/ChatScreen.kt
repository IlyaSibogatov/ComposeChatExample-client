package com.example.composechatexample.screens.chat.chatdetails

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.toMutableStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Alignment.Companion.BottomEnd
import androidx.compose.ui.Alignment.Companion.BottomStart
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.example.composechatexample.R
import com.example.composechatexample.domain.model.Message
import com.example.composechatexample.domain.model.SendType
import com.example.composechatexample.screens.chat.chatdetails.model.ChatScreenEvent
import com.example.composechatexample.utils.CircularLoader
import com.example.composechatexample.utils.Constants
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnrememberedMutableState")
@RequiresApi(Build.VERSION_CODES.N)
@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalFoundationApi::class
)
@Composable
fun ChatScreen(
    navController: NavHostController,
    chatId: String?,
) {
    val context = LocalContext.current
    val viewModel: ChatViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    viewModel.updateChatId(chatId!!)
    CompositionLocalProvider(
        LocalOverscrollConfiguration provides null,
    ) {
        Scaffold(
            bottomBar = {
                Column {
                    if (uiState.value.editSelect) {
                        Row(
                            modifier = Modifier
                                .height(28.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .clickable(onClick = { viewModel.editSelect(false) }),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = CenterVertically
                        ) {
                            Text(
                                modifier = Modifier
                                    .align(CenterVertically),
                                text = stringResource(id = R.string.edit_message_label),
                                fontSize = 18.sp,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                            Icon(
                                modifier = Modifier
                                    .align(CenterVertically),
                                painter = painterResource(R.drawable.ic_clear),
                                contentDescription = Constants.CONTENT_DESCRIPTION,
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                        Divider(
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    SendingField(
                        value = uiState.value.message,
                        onValueChange = viewModel::updateTypedMessage
                    ) {
                        viewModel.sendMessage(SendType.SEND)
                    }
                }
            }
        ) { padding ->
            LazyColumn(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxHeight(),
                reverseLayout = true,
                verticalArrangement = Arrangement.Bottom,
                horizontalAlignment = Alignment.End
            ) {
                items(uiState.value.messages.toMutableStateList()) { item ->
                    if (item.myMessage) {
                        val expandedMenu = remember { mutableStateOf(false) }
                        MyMessage(
                            content = {
                                ContentMessage(
                                    colorText = MaterialTheme.colorScheme.onTertiaryContainer,
                                    data = item
                                )
                            },
                            menu = {
                                ShowMenuMessage(
                                    expanded = expandedMenu,
                                    onCLick = { type ->
                                        viewModel.updateSelectedItem(item.id)
                                        when (type) {
                                            SendType.EDIT -> viewModel.prepareEdit(item.message)
                                            SendType.REMOVE -> viewModel.sendMessage(type)
                                            else -> {}
                                        }
                                    }
                                )
                            },
                            onAvatarCLick = { viewModel.navigateToProfile() }
                        ) { expandedMenu.value = !expandedMenu.value }
                    } else {
                        GuestMessage(
                            onAvatarCLick = { viewModel.navigateToProfile(item.userId) }
                        ) {
                            ContentMessage(
                                data = item,
                                colorText = MaterialTheme.colorScheme.onSecondaryContainer,
                            )
                        }
                    }
                }
            }
        }

    }

    if (uiState.value.isLoading) CircularLoader()
    LaunchedEffect(key1 = Unit) {
        viewModel.eventsFlow.collectLatest { value ->
            when (value) {
                is ChatScreenEvent.NavigateTo -> navController.navigate(value.route)
                is ChatScreenEvent.ToastEvent -> {
                    Toast.makeText(context, value.msg, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
    val lifeCycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START) viewModel.connectToChat()
            else if (event == Lifecycle.Event.ON_PAUSE) viewModel.socketDisconnect()
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SendingField(
    value: String,
    onValueChange: (String) -> Unit,
    onSendClick: () -> Unit
) {
    Column(verticalArrangement = Arrangement.Bottom) {
        TextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = value,
            onValueChange = onValueChange,
            textStyle = MaterialTheme.typography.bodyLarge,
            label = {
                Text(
                    text = stringResource(id = R.string.enter_message),
                    color = MaterialTheme.colorScheme.onSecondaryContainer
                )
            },
            trailingIcon = {
                Icon(
                    modifier = Modifier
                        .clickable { onSendClick() },
                    painter = painterResource(R.drawable.ic_send_message),
                    contentDescription = Constants.CONTENT_DESCRIPTION,
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            maxLines = 5,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        )
    }
}

@Preview
@Composable
private fun PreviewSend() {
    SendingField("", {}) {}
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MyMessage(
    content: @Composable () -> Unit,
    menu: @Composable () -> Unit,
    onAvatarCLick: () -> Unit,
    onLongClick: () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 14.dp, vertical = 8.dp)
            .fillMaxWidth(),
        contentAlignment = BottomEnd
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.End
        ) {
            Spacer(modifier = Modifier.weight(1f))
            Card(
                modifier = Modifier
                    .padding(end = 32.dp)
                    .wrapContentWidth()
                    .combinedClickable(
                        onClick = {},
                        onLongClick = {
                            onLongClick()
                        }
                    ),
                shape = MaterialTheme.shapes.small,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.tertiaryContainer
                ),

                ) {
                content()
            }
        }
        menu()
        Icon(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .clickable { onAvatarCLick() },
            painter = painterResource(id = R.drawable.ic_user),
            contentDescription = ""
        )
    }
}

@Composable
private fun GuestMessage(
    onAvatarCLick: () -> Unit,
    content: @Composable () -> Unit,
) {
    Box(
        modifier = Modifier
            .padding(horizontal = 14.dp, vertical = 8.dp),
        contentAlignment = BottomStart
    ) {
        Row(
            verticalAlignment = Alignment.Bottom,
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .clickable { onAvatarCLick() },
                painter = painterResource(id = R.drawable.ic_user),
                contentDescription = ""
            )
            Card(
                modifier = Modifier
                    .padding(start = 8.dp)
                    .wrapContentWidth(),
                shape = MaterialTheme.shapes.small,
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                )
            ) {
                content()
            }
            Spacer(modifier = Modifier.weight(1f))
        }
    }
}

@Composable
private fun ContentMessage(
    modifier: Modifier = Modifier.padding(horizontal = 8.dp),
    data: Message,
    colorText: Color
) {
    Text(
        modifier = modifier.padding(top = 4.dp),
        text = data.username,
        fontSize = 16.sp,
        color = colorText
    )
    Text(
        modifier = modifier.padding(vertical = 4.dp),
        text = data.message,
        fontSize = 14.sp,
        color = colorText,
        style = MaterialTheme.typography.bodyMedium
    )
    var edit = ""
    if (data.wasEdit) {
        edit = stringResource(id = R.string.message_was_edit) + " "
    }
    Text(
        modifier = modifier
            .padding(bottom = 4.dp),
        text = edit + data.formattedTime,
        fontSize = 10.sp,
        color = colorText,
        style = MaterialTheme.typography.bodyMedium
    )
}

@Composable
private fun ShowMenuMessage(
    expanded: MutableState<Boolean>,
    onCLick: (type: SendType) -> Unit,
) {
    Column(
        horizontalAlignment = Alignment.End
    ) {
        DropdownMenu(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background),
            expanded = expanded.value,
            onDismissRequest = {
                expanded.value = false
            }
        ) {
            MenuItem(
                name = stringResource(id = R.string.edit_message),
                expanded = expanded
            ) {
                onCLick(SendType.EDIT)
            }

            MenuItem(
                name = stringResource(id = R.string.remove_message),
                expanded = expanded
            ) {
                onCLick(SendType.REMOVE)
            }
        }
    }
}

@Composable
private fun MenuItem(
    name: String,
    expanded: MutableState<Boolean>,
    onCLick: () -> Unit
) {
    DropdownMenuItem(
        text = {
            Text(
                text = name,
                color = MaterialTheme.colorScheme.onBackground
            )
        },
        onClick = {
            expanded.value = false
            onCLick()
        },
        colors = MenuDefaults.itemColors(
            textColor = MaterialTheme.colorScheme.onBackground
        )
    )
}

@Preview
@Composable
private fun PreviewItemGuest() {
    GuestMessage(
        onAvatarCLick = {}
    ) {
        ContentMessage(
            data = FakeMessage,
            colorText = MaterialTheme.colorScheme.onTertiaryContainer
        )
    }
}

@Preview
@Composable
private fun PreviewItemMy() {
    MyMessage(
        content = {
            ContentMessage(
                data = FakeMessage,
                colorText = MaterialTheme.colorScheme.onSecondaryContainer,
            )
        },
        menu = {},
        onAvatarCLick = {}
    ) {}
}

val FakeMessage = Message(
    id = "123",
    message = "message",
    userId = "",
    username = "user",
    formattedTime = "22.03.12 12.35",
    myMessage = true,
    wasEdit = true,
)