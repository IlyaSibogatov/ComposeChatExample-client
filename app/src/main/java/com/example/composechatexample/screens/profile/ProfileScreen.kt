package com.example.composechatexample.screens.profile

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
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.composechatexample.R
import com.example.composechatexample.screens.profile.model.ProfileScreenEvent
import com.example.composechatexample.utils.Constants
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalGlideComposeApi::class, ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        val (
            photo, username, accountTw, nameET, numberET, emailET,
            friendListEt, friendListLc, editBtn,
        ) = createRefs()
        Card(
            modifier = Modifier
                .size(150.dp)
                .constrainAs(photo) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            shape = CircleShape,
        ) {
            GlideImage(
                modifier = Modifier.fillMaxSize(),
                model = R.drawable.ic_user,
                contentDescription = Constants.CONTENT_DESCRIPTION
            )
        }
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp)
                .constrainAs(username) {
                    top.linkTo(photo.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            text = uiState.value.username,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .constrainAs(accountTw) {
                    top.linkTo(username.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            text = stringResource(id = R.string.account_information),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )

        TextField(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .constrainAs(nameET) {
                    top.linkTo(accountTw.bottom)
                    start.linkTo(parent.start)
                },
            value = uiState.value.name,
            onValueChange = viewModel::changeName,
            enabled = uiState.value.canEditProfile,
            leadingIcon = {
                Text(text = stringResource(id = R.string.name_label))
            },
            textStyle = TextStyle.Default.copy(fontSize = 16.sp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                textColor = Color.Black,
            ),
            singleLine = true,
        )
        TextField(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .constrainAs(numberET) {
                    start.linkTo(parent.start)
                    top.linkTo(nameET.bottom)
                },
            value = uiState.value.number,
            onValueChange = viewModel::changeNumber,
            enabled = uiState.value.canEditProfile,
            leadingIcon = {
                Text(text = stringResource(id = R.string.number_label))
            },
            textStyle = TextStyle.Default.copy(fontSize = 16.sp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                textColor = Color.Black,
            ),
            singleLine = true,
        )
        TextField(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .fillMaxWidth()
                .constrainAs(emailET) {
                    start.linkTo(parent.start)
                    top.linkTo(numberET.bottom)
                },
            value = uiState.value.email,
            onValueChange = viewModel::changeEmail,
            enabled = uiState.value.canEditProfile,
            leadingIcon = {
                Text(text = stringResource(id = R.string.email_label))
            },
            textStyle = TextStyle.Default.copy(fontSize = 16.sp),
            colors = TextFieldDefaults.textFieldColors(
                containerColor = Color.Transparent,
                textColor = Color.Black,
            ),
            singleLine = true,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .constrainAs(friendListEt) {
                    start.linkTo(parent.start)
                    top.linkTo(emailET.bottom)
                },
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.friend_list),
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            Button(
                onClick = viewModel::openFriendList
            ) {
                Text(
                    text = stringResource(id = R.string.string_open),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                )
            }
        }
        LazyColumn(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .constrainAs(friendListLc) {
                    top.linkTo(friendListEt.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                    height = Dimension.fillToConstraints
                }
        ) {
            items(uiState.value.friendList.take(3)) { item ->
                Card(
                    modifier = Modifier
                        .padding(bottom = 5.dp),
                    onClick = {
                        /**
                        Переход на профиль друга
                         */
                    }
                ) {
                    ConstraintLayout(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val (
                            profilePhoto, friendName, onlineStatus, chatWithFriend
                        ) = createRefs()
                        GlideImage(
                            modifier = Modifier
                                .padding(8.dp)
                                .constrainAs(profilePhoto) {
                                    start.linkTo(parent.start)
                                },
                            model = R.drawable.ic_user,
                            contentDescription = Constants.CONTENT_DESCRIPTION,
                        )
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
                        GlideImage(
                            modifier = Modifier
                                .border(2.dp, Color.Gray, CircleShape)
                                .constrainAs(onlineStatus) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    start.linkTo(friendName.end)
                                    end.linkTo(chatWithFriend.start)
                                },
                            model = if (item.isOnline) R.drawable.ic_user_online
                            else R.drawable.ic_user_offline,
                            contentDescription = Constants.CONTENT_DESCRIPTION
                        )
                        GlideImage(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .clipToBounds()
                                .clickable(onClick = { /*TODO*/ })
                                .padding(horizontal = 8.dp)
                                .constrainAs(chatWithFriend) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    end.linkTo(parent.end)
                                    height = Dimension.fillToConstraints
                                },
                            model = R.drawable.ic_chat_with_friend,
                            contentDescription = Constants.CONTENT_DESCRIPTION
                        )
                    }
                }
            }
        }
        IconButton(
            modifier = Modifier
                .constrainAs(editBtn) {
                    top.linkTo(parent.top)
                    end.linkTo(parent.end)
                },
            onClick = viewModel::allowCorrection
        ) {
            Icon(
                painter = painterResource(id = R.drawable.ic_edit_data),
                contentDescription = Constants.CONTENT_DESCRIPTION
            )
        }
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.eventsFlow.collectLatest { value ->
            when (value) {
                is ProfileScreenEvent.NavigateTo -> {
                    if (value.route == Constants.ONBOARD_ROUTE) {
                        navController.navigate(value.route) {
                            popUpTo(0)
                        }
                    }
                    if (value.route == Constants.FRIENDS_LIST_ROUTE) {
                        navController.navigate(value.route)
                    }
                }
            }
        }
    }
}