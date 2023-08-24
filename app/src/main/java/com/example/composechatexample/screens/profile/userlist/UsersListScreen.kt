package com.example.composechatexample.screens.profile.userlist

import android.annotation.SuppressLint
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.composechatexample.R
import com.example.composechatexample.screens.profile.userlist.model.UsersListEvent
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.Constants.FRIENDSHIPS_REQUESTS
import kotlinx.coroutines.flow.collectLatest

@SuppressLint("CheckResult")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UsersListScreen(
    navController: NavHostController,
    uid: String? = null,
    type: String? = null
) {
    val viewModel: UsersListViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    viewModel.getFollowerFriends(
        uid?.removePrefix(Constants.USER_UID),
        type?.removePrefix(Constants.USERS_TYPE)
    )
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(uiState.value.usersList) { item ->
            Card(
                modifier = Modifier
                    .padding(bottom = 5.dp),
                colors = CardDefaults.cardColors(
                    containerColor =
                    MaterialTheme.colorScheme.primaryContainer
                ),
                onClick = { viewModel.openProfile(item.id) }
            ) {
                ConstraintLayout(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    val (
                        profilePhoto, friendName, onlineStatus, chatWithFriend
                    ) = createRefs()
                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                            .constrainAs(profilePhoto) {
                                start.linkTo(parent.start)
                            },
                        shape = CircleShape,
                    ) {
                        AsyncImage(
                            model = ImageRequest.Builder(LocalContext.current)
                                .data(Constants.BASE_URL + "/images/" + item.id + ".jpeg")
                                .networkCachePolicy(CachePolicy.READ_ONLY)
                                .diskCachePolicy(CachePolicy.DISABLED)
                                .memoryCachePolicy(CachePolicy.WRITE_ONLY)
                                .build(),
                            alignment = Alignment.Center,
                            contentScale = ContentScale.Crop,
                            contentDescription = Constants.CONTENT_DESCRIPTION,
                            placeholder = painterResource(id = R.drawable.ic_user),
                            error = painterResource(id = R.drawable.ic_user),
                        )
                    }
                    Text(
                        modifier = Modifier
                            .padding(8.dp)
                            .constrainAs(friendName) {
                                start.linkTo(profilePhoto.end)
                                end.linkTo(onlineStatus.start)
                                width = Dimension.fillToConstraints
                            },
                        text = item.username,
                        fontSize = 16.sp,
                        maxLines = 1,
                    )
                    when (uiState.value.screenType) {
                        FRIENDSHIPS_REQUESTS -> {
                            Row(
                                modifier = Modifier
                                    .constrainAs(chatWithFriend) {
                                        top.linkTo(parent.top)
                                        bottom.linkTo(parent.bottom)
                                        start.linkTo(friendName.end)
                                        end.linkTo(parent.end)
                                        height = Dimension.fillToConstraints
                                        width = Dimension.fillToConstraints
                                    },
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Text(
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.friendshipsAccept(item.id, true)
                                        },
                                    text = stringResource(id = R.string.accept_label),
                                    color = Color(0XFF304f00),
                                    textAlign = TextAlign.Center,
                                )
                                Text(
                                    modifier = Modifier
                                        .clickable {
                                            viewModel.friendshipsAccept(item.id, false)
                                        },
                                    text = stringResource(id = R.string.decline),
                                    color = Color(0xFF93000a),
                                    textAlign = TextAlign.Center,
                                )
                            }
                        }

                        else -> {
                            Image(
                                modifier = Modifier
                                    .size(24.dp)
                                    .border(2.dp, Color.Gray, CircleShape)
                                    .constrainAs(onlineStatus) {
                                        top.linkTo(parent.top)
                                        bottom.linkTo(parent.bottom)
                                        start.linkTo(friendName.end)
                                        end.linkTo(chatWithFriend.start)
                                    },
                                painter = painterResource(
                                    id =
                                    if (item.onlineStatus) R.drawable.ic_user_online
                                    else R.drawable.ic_user_offline
                                ),
                                contentDescription = Constants.CONTENT_DESCRIPTION
                            )
                            /** Go to friend chat */
                            IconButton(
                                modifier = Modifier
                                    .padding(horizontal = 8.dp)
                                    .padding(start = 4.dp)
                                    .size(24.dp)
                                    .clipToBounds()
                                    .constrainAs(chatWithFriend) {
                                        top.linkTo(parent.top)
                                        bottom.linkTo(parent.bottom)
                                        end.linkTo(parent.end)
                                        height = Dimension.fillToConstraints
                                    },
                                onClick = { }
                            ) {
                                Icon(
                                    painter = painterResource(
                                        id = R.drawable.ic_chat_with_friend
                                    ),
                                    contentDescription = Constants.CONTENT_DESCRIPTION
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.eventsFlow.collectLatest { value ->
            when (value) {
                is UsersListEvent.NavigateTo -> {
                    if (value.route == "popBackStack")
                        navController.popBackStack()
                    else
                        navController.navigate(value.route)
                }
            }
        }
    }
}