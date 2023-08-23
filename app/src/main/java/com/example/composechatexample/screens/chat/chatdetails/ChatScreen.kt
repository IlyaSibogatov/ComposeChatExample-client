package com.example.composechatexample.screens.chat.chatdetails

import android.annotation.SuppressLint
import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
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
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.composechatexample.R
import com.example.composechatexample.components.CircularLoader
import com.example.composechatexample.components.CustomIconButton
import com.example.composechatexample.components.ShowMenu
import com.example.composechatexample.data.model.UserChatInfo
import com.example.composechatexample.domain.model.Message
import com.example.composechatexample.screens.chat.chatdetails.model.ChatScreenEvent
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.SendType
import com.example.composechatexample.utils.Type
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnrememberedMutableState")
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
                                .background(MaterialTheme.colorScheme.primaryContainer),
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
                            CustomIconButton(
                                modifier = Modifier.align(CenterVertically),
                                imageId = R.drawable.ic_clear,
                                color = MaterialTheme.colorScheme.primary,
                                onClick = { viewModel.editSelect(false) }
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
                                    data = item,
                                    user = uiState.value.usersInfo.find { it.uuid == item.userId }
                                )
                            },
                            menu = {
                                ShowMenu(
                                    expanded = expandedMenu,
                                    data = listOf(
                                        Type(nameType = SendType.EDIT, str = R.string.edit),
                                        Type(nameType = SendType.REMOVE, str = R.string.remove),
                                    ),
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
                        ) { expandedMenu.value = !expandedMenu.value }
                    } else {
                        GuestMessage(
                            onAvatarCLick = { viewModel.navigateToProfile(item.userId) },
                            uuid = item.userId,
                        ) {
                            ContentMessage(
                                data = item,
                                colorText = MaterialTheme.colorScheme.onSecondaryContainer,
                                user = uiState.value.usersInfo.find { it.uuid == item.userId }
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
                    color = MaterialTheme.colorScheme.onPrimary
                )
            },
            trailingIcon = {
                CustomIconButton(
                    imageId = R.drawable.ic_send_message,
                    color = MaterialTheme.colorScheme.onPrimary,
                    onClick = { onSendClick() }
                )
            },
            maxLines = 5,
            colors = TextFieldDefaults.textFieldColors(
                containerColor = MaterialTheme.colorScheme.primary,
                textColor = MaterialTheme.colorScheme.onPrimary
            )
        )
    }
}

@Preview
@Composable
private fun PreviewSend() {
    SendingField("", {}) {}
}

@SuppressLint("CheckResult")
@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun MyMessage(
    content: @Composable () -> Unit,
    menu: @Composable () -> Unit,
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
    }
}

@SuppressLint("CheckResult")
@Composable
private fun GuestMessage(
    onAvatarCLick: () -> Unit,
    uuid: String,
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
            Card(
                modifier = Modifier
                    .size(36.dp),
                shape = CircleShape,
            ) {
                AsyncImage(
                    modifier = Modifier
                        .fillMaxSize()
                        .clickable { onAvatarCLick() },
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(Constants.BASE_URL + "/images/" + uuid + ".jpeg")
                        .networkCachePolicy(CachePolicy.READ_ONLY)
                        .diskCachePolicy(CachePolicy.READ_ONLY)
                        .memoryCachePolicy(CachePolicy.DISABLED)
                        .build(),
                    alignment = Alignment.Center,
                    contentScale = ContentScale.Crop,
                    contentDescription = Constants.CONTENT_DESCRIPTION,
                    placeholder = painterResource(id = R.drawable.ic_user),
                    error = painterResource(id = R.drawable.ic_user),
                )
            }
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
    user: UserChatInfo?,
    colorText: Color
) {
    Text(
        modifier = modifier.padding(top = 4.dp),
        text = user?.username ?: "",
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

@Preview
@Composable
private fun PreviewItemGuest() {
    GuestMessage(
        onAvatarCLick = {},
        uuid = "",
    ) {
        ContentMessage(
            data = FakeMessage,
            colorText = MaterialTheme.colorScheme.onTertiaryContainer,
            user = UserChatInfo(
                "", ""
            )
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
                user = UserChatInfo(
                    "", ""
                )
            )
        },
        menu = {},
    ) {}
}

val FakeMessage = Message(
    id = "123",
    message = "message",
    userId = "",
    formattedTime = "22.03.12 12.35",
    myMessage = true,
    wasEdit = true,
)