package com.example.composechatexample.screens.chat.chatdetails

import android.annotation.SuppressLint
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.LocalOverscrollConfiguration
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.composechatexample.R
import com.example.composechatexample.domain.model.SendType
import com.example.composechatexample.screens.chat.chatdetails.model.ChatScreenEvent
import com.example.composechatexample.ui.theme.LightRed80
import com.example.composechatexample.ui.theme.SmoothGrey
import com.example.composechatexample.utils.CircularLoader
import com.example.composechatexample.utils.Constants
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("UnrememberedMutableState")
@RequiresApi(Build.VERSION_CODES.N)
@OptIn(
    ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class,
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
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
        ) {
            val (
                lcMessages, inputField, textField, sendIcon, image, editIndicator,
            ) = createRefs()
            LazyColumn(
                modifier = Modifier
                    .padding(vertical = 8.dp)
                    .constrainAs(lcMessages) {
                        top.linkTo(parent.top)
                        bottom.linkTo(
                            if (uiState.value.editSelect)
                                editIndicator.top
                            else
                                inputField.top
                        )
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                        height = Dimension.fillToConstraints
                        width = Dimension.fillToConstraints
                    },
                reverseLayout = true,
            ) {
                items(uiState.value.messages.toMutableStateList()) { item ->
                    ConstraintLayout(
                        modifier = Modifier
                            .fillParentMaxWidth()
                            .padding(12.dp),
                    ) {
                        val (
                            messageCard, nameTV, messageTV, dateTV, menuDots, editedTV
                        ) = createRefs()
                        IconButton(
                            modifier = Modifier
                                .size(48.dp)
                                .padding()
                                .constrainAs(image) {
                                    if (item.myMessage) end.linkTo(parent.end)
                                    else start.linkTo(parent.start)
                                    bottom.linkTo(messageCard.bottom)
                                },
                            onClick = viewModel::navigateToProfile
                        ) {
                            GlideImage(
                                model = R.drawable.ic_user,
                                contentDescription = Constants.CONTENT_DESCRIPTION,
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(
                                    start = if (item.myMessage) 18.dp else 0.dp,
                                    end = if (!item.myMessage) 18.dp else 0.dp,
                                )
                                .constrainAs(messageCard) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    if (item.myMessage) {
                                        start.linkTo(parent.start)
                                        end.linkTo(image.start)
                                    } else {
                                        start.linkTo(image.end)
                                        end.linkTo(parent.end)
                                    }
                                    width = Dimension.fillToConstraints
                                },
                        ) {
                            if (item.myMessage) Spacer(modifier = Modifier.weight(1f))
                            Card(
                                modifier = Modifier
                                    .wrapContentWidth(),
                                colors = CardDefaults.cardColors(
                                    containerColor =
                                    if (item.myMessage) LightRed80
                                    else Color.Gray
                                ),
                                shape = MaterialTheme.shapes.small,
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 5.dp
                                ),
                            ) {
                                ConstraintLayout(
                                    modifier = Modifier
                                        .padding(6.dp)
                                ) {
                                    val expanded = remember { mutableStateOf(false) }
                                    Text(
                                        modifier = Modifier
                                            .padding(5.dp)
                                            .constrainAs(nameTV) {
                                                top.linkTo(parent.top)
                                                start.linkTo(parent.start)
                                                if (item.myMessage) {
                                                    end.linkTo(menuDots.start)
                                                    bottom.linkTo(menuDots.bottom)
                                                } else {
                                                    end.linkTo(parent.end)
                                                }
                                                width = Dimension.fillToConstraints
                                            },
                                        textAlign = TextAlign.Start,
                                        text = item.username,
                                        fontSize = 20.sp,
                                    )
                                    if (item.myMessage) {
                                        Box(
                                            modifier = Modifier
                                                .constrainAs(menuDots) {
                                                    top.linkTo(parent.top)
                                                    end.linkTo(parent.end)
                                                }
                                        ) {
                                            IconButton(
                                                onClick = {
                                                    expanded.value = !expanded.value
                                                    viewModel.updateSelectedItem(item.id)
                                                }) {
                                                GlideImage(
                                                    model = R.drawable.ic_more_vert,
                                                    contentDescription = Constants.CONTENT_DESCRIPTION,
                                                )
                                            }
                                            DropdownMenu(
                                                expanded = expanded.value,
                                                onDismissRequest = {
                                                    expanded.value = false
                                                }
                                            ) {
                                                DropdownMenuItem(
                                                    text = { Text(text = stringResource(id = R.string.edit_message)) },
                                                    onClick = {
                                                        expanded.value = false
                                                        viewModel.prepareEdit(item.message)
                                                    }
                                                )
                                                DropdownMenuItem(
                                                    text = { Text(text = stringResource(id = R.string.remove_message)) },
                                                    onClick = {
                                                        expanded.value = false
                                                        viewModel.sendMessage(SendType.REMOVE)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                    Text(
                                        modifier = Modifier
                                            .padding(
                                                horizontal = 5.dp,
                                                vertical = 8.dp
                                            )
                                            .constrainAs(messageTV) {
                                                top.linkTo(nameTV.bottom)
                                                start.linkTo(nameTV.start)
                                                width = Dimension.wrapContent
                                            },
                                        textAlign = TextAlign.Start,
                                        text = item.message,
                                        fontSize = 20.sp,
                                    )
                                    if (item.wasEdit) {
                                        Text(
                                            modifier = Modifier
                                                .padding(
                                                    bottom = 5.dp,
                                                    start = 5.dp,
                                                    end = 5.dp,
                                                )
                                                .constrainAs(editedTV) {
                                                    end.linkTo(parent.end)
                                                    top.linkTo(messageTV.bottom)
                                                },
                                            text = stringResource(id = R.string.message_was_edit),
                                            color = SmoothGrey,
                                            fontSize = 18.sp,
                                            fontStyle = FontStyle.Italic
                                        )
                                    }
                                    Text(
                                        modifier = Modifier
                                            .padding(
                                                bottom = 5.dp,
                                                start = 5.dp,
                                                end = 5.dp,
                                            )
                                            .constrainAs(dateTV) {
                                                if (item.wasEdit)
                                                    top.linkTo(editedTV.bottom)
                                                else
                                                    top.linkTo(messageTV.bottom)
                                                bottom.linkTo(parent.bottom)
                                                end.linkTo(parent.end)
                                            },
                                        text = item.formattedTime,
                                        fontSize = 16.sp,
                                    )
                                }
                            }
                            if (!item.myMessage) Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }
            }
            if (uiState.value.editSelect) {
                Row(
                    modifier = Modifier
                        .heightIn(0.dp, 48.dp)
                        .background(SmoothGrey)
                        .padding(
                            horizontal = 8.dp,
                            vertical = 5.dp
                        )
                        .constrainAs(editIndicator) {
                            bottom.linkTo(inputField.top)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                            height = Dimension.wrapContent
                            width = Dimension.fillToConstraints
                        }
                        .clickable(onClick = { viewModel.editSelect(false) }),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        modifier = Modifier
                            .align(CenterVertically),
                        text = stringResource(id = R.string.edit_message_label),
                        fontSize = 18.sp
                    )
                    GlideImage(
                        modifier = Modifier
                            .align(CenterVertically),
                        model = R.drawable.ic_clear,
                        contentDescription = Constants.CONTENT_DESCRIPTION,
                    )
                }
            }
            ConstraintLayout(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.LightGray)
                    .padding(8.dp)
                    .constrainAs(inputField) {
                        bottom.linkTo(parent.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    }
            ) {
                TextField(
                    modifier = Modifier
                        .padding(end = 8.dp)
                        .constrainAs(textField) {
                            bottom.linkTo(parent.bottom)
                            start.linkTo(parent.start)
                            end.linkTo(sendIcon.start)
                            width = Dimension.fillToConstraints
                        },
                    value = uiState.value.message,
                    onValueChange = viewModel::updateTypedMessage,
                    colors = TextFieldDefaults.textFieldColors(
                        containerColor = Color.White,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                    ),
                    label = { Text(stringResource(id = R.string.enter_message)) },
                    shape = MaterialTheme.shapes.small
                )
                IconButton(
                    modifier = Modifier
                        .constrainAs(sendIcon) {
                            top.linkTo(parent.top)
                            end.linkTo(parent.end)
                            bottom.linkTo(parent.bottom)
                        },
                    enabled = !uiState.value.onSending,
                    onClick = {
                        viewModel.sendMessage(SendType.SEND)
                    },
                ) {
                    GlideImage(
                        model = R.drawable.ic_send_message,
                        contentDescription = Constants.CONTENT_DESCRIPTION,
                    )
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