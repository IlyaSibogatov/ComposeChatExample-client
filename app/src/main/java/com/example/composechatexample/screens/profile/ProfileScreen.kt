package com.example.composechatexample.screens.profile

import android.annotation.SuppressLint
import android.graphics.Bitmap
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
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
import com.example.composechatexample.screens.dialogs.EditInfoDialog
import com.example.composechatexample.screens.profile.model.ProfileScreenEvent
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.Constants.FOLLOWERS
import com.example.composechatexample.utils.Constants.FRIENDS
import com.example.composechatexample.utils.Constants.FRIENDSHIPS_REQUESTS
import com.example.composechatexample.utils.Ext.showToast
import com.example.composechatexample.utils.ResponseStatus
import kotlinx.coroutines.flow.collectLatest
import java.io.ByteArrayOutputStream

@SuppressLint("CheckResult")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavHostController,
    uid: String? = null,
) {
    val context = LocalContext.current
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(context.contentResolver, it)
            val baos = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.JPEG, 25, baos)
            val fileInBytes = baos.toByteArray()
            viewModel.updateImage(it)
            viewModel.setAvatar(fileInBytes)
        }
    }
    viewModel.getProfile(uid?.removePrefix(Constants.USER_UID))

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        val (
            photo, username, friendListEt, friendListLc, selfInfoPreview,
            editBtn, showMore, selfInfo, addToFriend, userStatus, followerAndRequests
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
            AsyncImage(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable {
                        if (viewModel.isMyProfile())
                            launcher.launch("image/*")
                    },
                model = ImageRequest.Builder(LocalContext.current)
                    .data(
                        if (uiState.value.updateImage)
                            uiState.value.imageUri
                        else Constants.BASE_URL + "/images/" + uiState.value.uid + ".jpeg"
                    )
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
        Image(
            modifier = Modifier
                .padding(top = 25.dp, end = 25.dp)
                .size(16.dp)
                .border(2.dp, Color.Gray, CircleShape)
                .constrainAs(userStatus) {
                    top.linkTo(photo.top)
                    end.linkTo(photo.end)
                },
            painter = painterResource(
                id = if (uiState.value.onlineStatus) R.drawable.ic_user_online
                else R.drawable.ic_user_offline
            ),
            contentDescription = Constants.CONTENT_DESCRIPTION
        )
        Text(
            modifier = Modifier
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
                .constrainAs(selfInfoPreview) {
                    top.linkTo(username.bottom)
                    start.linkTo(parent.start)
                },
            text = stringResource(id = R.string.user_info_label),
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
        )

        Text(
            modifier = Modifier
                .padding(bottom = 8.dp)
                .wrapContentWidth()
                .constrainAs(selfInfo) {
                    top.linkTo(selfInfoPreview.bottom)
                    start.linkTo(parent.start)
                },
            text = uiState.value.selfInfo.ifEmpty {
                stringResource(id = R.string.info_dont_filled)
            },
            fontSize = 16.sp,
            maxLines = if (!uiState.value.showMoreInfo) 3 else Int.MAX_VALUE,
            overflow = if (!uiState.value.showMoreInfo) TextOverflow.Ellipsis else TextOverflow.Clip,
            onTextLayout = {
                viewModel.selfInfoOverflowed(it.hasVisualOverflow)
            }
        )
        if (uiState.value.infoOverflowed || uiState.value.showMoreInfo) {
            Text(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(bottom = 8.dp, start = 4.dp, end = 4.dp)
                    .clickable {
                        viewModel.showMoreInfo()
                    }
                    .constrainAs(showMore) {
                        if (uiState.value.showMoreInfo)
                            top.linkTo(selfInfo.bottom)
                        else
                            bottom.linkTo(selfInfo.bottom)
                        end.linkTo(selfInfo.end)
                    },
                color = Color.Blue,
                text = stringResource(
                    id = if (uiState.value.showMoreInfo)
                        R.string.roll_up
                    else R.string.show_more
                ),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp)
                .constrainAs(friendListEt) {
                    start.linkTo(parent.start)
                    top.linkTo(followerAndRequests.bottom)
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
        }
        Row(
            modifier = Modifier
                .constrainAs(followerAndRequests) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    if (uiState.value.showMoreInfo)
                        top.linkTo(showMore.bottom)
                    else
                        top.linkTo(selfInfo.bottom)
                }
        ) {
            if (uiState.value.followers.isNotEmpty())
                Card(
                    modifier = Modifier
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor =
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                    onClick = {
                        if (uiState.value.followers.isNotEmpty()) viewModel.openUsersList(
                            FOLLOWERS
                        )
                    },
                ) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp),
                        text = "${uiState.value.followers.size} followers",
                        textAlign = TextAlign.Center,
                    )
                }
            if (uiState.value.friendshipRequests.isNotEmpty() && viewModel.isMyProfile()) {
                Spacer(modifier = Modifier.width(8.dp))
                Card(
                    modifier = Modifier
                        .weight(1f),
                    colors = CardDefaults.cardColors(
                        containerColor =
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                    onClick = {
                        if (uiState.value.friendshipRequests.isNotEmpty()) viewModel.openUsersList(
                            FRIENDSHIPS_REQUESTS
                        )
                    },
                ) {
                    Text(
                        modifier = Modifier
                            .padding(8.dp),
                        text = "${uiState.value.friendshipRequests.size} requests",
                        textAlign = TextAlign.Center,
                    )
                }
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
            items(uiState.value.friends.take(5)) { item ->
                Card(
                    modifier = Modifier
                        .padding(bottom = 1.dp),
                    colors = CardDefaults.cardColors(
                        containerColor =
                        MaterialTheme.colorScheme.primaryContainer
                    ),
                    onClick = {
                        viewModel.openProfile(item.id)
                    },
                ) {
                    ConstraintLayout(
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val (
                            profilePhoto, friendName, onlineStatus, chatWithFriend
                        ) = createRefs()
                        AsyncImage(
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp)
                                .clip(CircleShape)
                                .constrainAs(profilePhoto) {
                                    start.linkTo(parent.start)
                                },
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
                                id = if (item.onlineStatus) R.drawable.ic_user_online
                                else R.drawable.ic_user_offline
                            ),
                            contentDescription = Constants.CONTENT_DESCRIPTION
                        )
                        IconButton(
                            modifier = Modifier
                                .padding(start = 4.dp)
                                .padding(horizontal = 8.dp)
                                .size(24.dp)
                                .clipToBounds()
                                .constrainAs(chatWithFriend) {
                                    top.linkTo(parent.top)
                                    bottom.linkTo(parent.bottom)
                                    end.linkTo(parent.end)
                                    height = Dimension.fillToConstraints
                                },
                            onClick = {
                                showToast(
                                    context = context,
                                    context.resources.getString(R.string.development)
                                )
                            }
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
            item {
                Card(
                    modifier = Modifier
                        .padding(bottom = 5.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    onClick = {
                        if (uiState.value.friends.isNotEmpty()) viewModel.openUsersList(FRIENDS)
                    },
                    elevation = CardDefaults.cardElevation(3.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        text = stringResource(id = R.string.view_friends),
                        textAlign = TextAlign.Center,
                        fontSize = 16.sp,
                        maxLines = 1,
                    )
                }
            }
        }
        if (viewModel.isMyProfile()) {
            IconButton(
                modifier = Modifier
                    .constrainAs(editBtn) {
                        top.linkTo(parent.top)
                        end.linkTo(parent.end)
                    },
                onClick = {
                    viewModel.showEditDialog()
                }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_edit_data),
                    contentDescription = Constants.CONTENT_DESCRIPTION
                )
            }
        }
        if (!viewModel.isFriend()) {
            IconButton(
                modifier = Modifier
                    .constrainAs(addToFriend) {
                        top.linkTo(parent.top)
                        start.linkTo(parent.start)
                    },
                onClick = { viewModel.friendshipRequest() }
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_person_add),
                    contentDescription = Constants.CONTENT_DESCRIPTION
                )
            }
        }
    }
    if (uiState.value.showEditDialog) {
        EditInfoDialog()
    }
    if (uiState.value.imageUploading) {
        LinearProgressIndicator(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
        )
    }
    LaunchedEffect(key1 = Unit) {
        viewModel.eventsFlow.collectLatest { value ->
            when (value) {
                is ProfileScreenEvent.NavigateTo -> {
                    navController.navigate(value.route)
                }

                is ProfileScreenEvent.ToastEvent -> {
                    showToast(
                        context,
                        when (value.msg) {
                            ResponseStatus.INFO_UPDATED.value ->
                                context.resources.getString(R.string.succes_data_update)

                            ResponseStatus.INFO_NOT_UPDATED.value ->
                                context.resources.getString(R.string.failed_data_update)

                            ResponseStatus.FRIENDSHIP_REQUEST_SEND.value ->
                                context.resources.getString(R.string.success_friend_request)

                            ResponseStatus.FRIENDSHIP_REQUEST_NOT_SEND.value ->
                                context.resources.getString(R.string.failed_friend_request)

                            ResponseStatus.FAILED.value ->
                                context.resources.getString(R.string.exception_toast)

                            else -> context.resources.getString(R.string.exception_toast)
                        }
                    )
                }
            }
        }
    }
}