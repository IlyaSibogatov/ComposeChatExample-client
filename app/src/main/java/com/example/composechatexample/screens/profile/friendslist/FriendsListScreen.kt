package com.example.composechatexample.screens.profile.friendslist

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.bumptech.glide.integration.compose.ExperimentalGlideComposeApi
import com.bumptech.glide.integration.compose.GlideImage
import com.example.composechatexample.R
import com.example.composechatexample.screens.profile.ProfileViewModel
import com.example.composechatexample.utils.Constants

@OptIn(ExperimentalMaterial3Api::class, ExperimentalGlideComposeApi::class)
@Composable
fun FriendsListScreen(
    navController: NavHostController,
) {
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(uiState.value.friendList) { item ->
            Card(
                modifier = Modifier
                    .padding(bottom = 5.dp),
                onClick = {
                    /**
                    Open friend profile
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
                            .clickable(onClick = {
                                /**
                                Go to friend chat
                                 */
                            })
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
}