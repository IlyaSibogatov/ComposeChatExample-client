package com.example.composechatexample.screens.profile

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.example.composechatexample.R
import com.example.composechatexample.components.CircularLoader
import com.example.composechatexample.components.CoilImage
import com.example.composechatexample.components.CustomIconButton
import com.example.composechatexample.components.EmptyOrErrorView
import com.example.composechatexample.components.ShowMenu
import com.example.composechatexample.domain.model.Friend
import com.example.composechatexample.screens.dialogs.EditInfoDialog
import com.example.composechatexample.screens.dialogs.FriendAddRemoveDialog
import com.example.composechatexample.screens.profile.model.ProfileScreenEvent
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.Constants.EMPTY_VALUE
import com.example.composechatexample.utils.Constants.FOLLOWERS
import com.example.composechatexample.utils.Constants.FRIENDSHIPS_REQUESTS
import com.example.composechatexample.utils.Constants.SPACE_VALUE
import com.example.composechatexample.utils.Ext
import com.example.composechatexample.utils.Ext.shareProfile
import com.example.composechatexample.utils.MediaType
import com.example.composechatexample.utils.ProfileDialogs
import com.example.composechatexample.utils.ResponseStatus
import com.example.composechatexample.utils.ScreenState
import com.example.composechatexample.utils.TypeMenuItem
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(
    navController: NavHostController,
    uid: String? = null,
) {

    val context = LocalContext.current
    val viewModel: ProfileViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.updateImage(it)
                viewModel.sendAvatar(Ext.getCompressedImage(it, context))
            }
        }
    }

    when (uiState.value.screenState) {

        ScreenState.INIT -> CircularLoader()

        ScreenState.ERROR -> {
            EmptyOrErrorView(
                isError = true
            )
        }

        ScreenState.SUCCESS -> {
            when (uiState.value.displayingView) {
                ProfileDialogs.ADD_FRIEND -> FriendAddRemoveDialog(true)

                ProfileDialogs.REMOVE_FRIEND -> FriendAddRemoveDialog()

                ProfileDialogs.PROGRESS_LINEAR -> LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(4.dp)
                )

                ProfileDialogs.EDIT_INFO -> EditInfoDialog()

                else -> {}
            }

            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                item {
                    AvatarAndButtons(
                        uiState.value.uid,
                        viewModel.isFriend(),
                        viewModel.isNotInFriendRequest(),
                        viewModel.isMyProfile(),
                        uiState.value.avatarId,
                        uiState.value.imageUri,
                        openPicker = {
                            imagePicker.launch("image/*")
                        },
                        menuItemClicked = {
                            when (it) {
                                TypeMenuItem.EDIT.name -> viewModel.showEditDialog()
                                TypeMenuItem.SHARE.name -> shareProfile(
                                    viewModel.uiState.value.uid,
                                    context
                                )

                                TypeMenuItem.UPLOAD_IMAGE.name -> {
                                    navController.navigate(
                                        Constants.UPLOAD_ROUTE + "/${MediaType.IMAGE.name}"
                                    )
                                }

                                TypeMenuItem.UPLOAD_VIDEO.name -> {
                                    navController.navigate(
                                        Constants.UPLOAD_ROUTE + "/${MediaType.VIDEO.name}"
                                    )
                                }
                            }
                        },
                        friendAction = viewModel::showAddRemoveDialog
                    )
                }
                item {
                    InformationLayout(
                        uiState.value.username,
                        uiState.value.selfInfo,
                        uiState.value.showMoreInfo,
                        uiState.value.infoOverflowed,
                        textOverFlowed = viewModel::selfInfoOverflowed,
                        moreInfoClick = viewModel::showMoreInfo,
                    )
                }
                item {
                    FollowersRequests(
                        viewModel.isMyProfile(),
                        uiState.value.followers.size,
                        uiState.value.friendshipRequests.size,
                        openUserList = viewModel::openUsersList
                    )
                }
                item {
                    MediaLayout(
                        uiState.value.uid,
                        uiState.value.photoList.map { it.id },
                        uiState.value.videoList.map { it.id },
                        viewModel.isMyProfile()
                    ) {

                    }
                }
                friendList(
                    uiState.value.friends,
                    openProfile = {
                        viewModel.openProfile(it)
                    },
                    openFriendList = {
                        viewModel.openUsersList(Constants.FRIENDS)
                    }
                )
            }
        }

        else -> {}
    }

    LaunchedEffect(key1 = Unit) {
        viewModel.eventsFlow.collectLatest { value ->
            when (value) {
                is ProfileScreenEvent.NavigateTo -> {
                    navController.navigate(value.route)
                }

                is ProfileScreenEvent.ToastEvent -> {
                    Ext.showToast(
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

                            ResponseStatus.FRIEND_REMOVED.value ->
                                context.resources.getString(R.string.friend_was_removed)

                            ResponseStatus.FAILED.value ->
                                context.resources.getString(R.string.exception_toast)

                            else -> context.resources.getString(R.string.exception_toast)
                        }
                    )
                }
            }
        }
    }
    val lifeCycleOwner = LocalLifecycleOwner.current

    DisposableEffect(key1 = lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START)
                viewModel.getProfile(uid?.removePrefix(Constants.USER_UID))
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}

@Composable
fun AvatarAndButtons(
    uuid: String,
    isFriend: Boolean,
    isNotFriendRequest: Boolean,
    isMyProfile: Boolean,
    avatarId: String,
    newUrl: Uri?,

    menuItemClicked: (itemName: String) -> Unit,
    openPicker: () -> Unit,
    friendAction: (isFriend: Boolean) -> Unit,
) {
    Box(
        modifier = Modifier.fillMaxWidth(),
        contentAlignment = Alignment.TopCenter
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            FriendIcon(
                isFriend,
                isNotFriendRequest,
                isMyProfile,
                iconClick = friendAction
            )
            ActionIcon(isMyProfile, menuItemClicked)
        }
        MainAvatar(isMyProfile, newUrl, uuid, avatarId, openPicker)
    }
}

@Composable
fun FriendIcon(
    isFriend: Boolean,
    isNotFriendRequest: Boolean,
    isMyProfile: Boolean,
    iconClick: (isFriend: Boolean) -> Unit
) {
    if (isFriend || isNotFriendRequest && !isMyProfile)
        CustomIconButton(
            imageId = if (isFriend) R.drawable.ic_remove_friend
            else R.drawable.ic_person_add,
            onClick = { iconClick(isFriend) }
        )
    else Spacer(modifier = Modifier)
}

@Composable
fun MainAvatar(
    isMyProfile: Boolean,
    imageUrl: Uri?,
    uuid: String,
    avatarId: String,

    onAvatarClick: () -> Unit,
) {
    Card(
        modifier = Modifier
            .size(150.dp),
        shape = CircleShape,
    ) {
        val url = Constants.BASE_URL + "/uploads/upload_photos/${uuid}/${avatarId}.jpeg"
        CoilImage(
            data = imageUrl
                ?: (url),
            modifier = Modifier
                .fillMaxSize()
                .clickable {
                    if (isMyProfile)
                        onAvatarClick()
                },
            R.drawable.ic_user
        )
    }
}

@Composable
fun ActionIcon(
    isMyProfile: Boolean = false,
    menuItemClick: (name: String) -> Unit,
) {
    if (isMyProfile) {
        Row() {
            val expanded = remember { mutableStateOf(false) }
            CustomIconButton(
                imageId = R.drawable.ic_menu_dots,
                onClick = { expanded.value = true }
            )

            ShowMenu(
                expanded = expanded,
                data = Constants.profileMenu,
                onCLick = { menuItemClick(it.name) },
            )
        }
    } else {
        CustomIconButton(
            imageId = R.drawable.ic_share,
            onClick = { menuItemClick(TypeMenuItem.SHARE.name) })
    }
}

@Composable
fun InformationLayout(
    name: String,
    selfInfo: String,
    showMoreInfo: Boolean,
    infoOverflowed: Boolean,
    textOverFlowed: (Boolean) -> Unit,
    moreInfoClick: () -> Unit,
) {
    Text(
        modifier = Modifier
            .padding(8.dp),
        text = name,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
    )
    Text(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        text = stringResource(id = R.string.user_info_label),
        fontSize = 18.sp,
        fontWeight = FontWeight.Bold,
    )
    Text(
        modifier = Modifier
            .fillMaxWidth(),
        text = selfInfo.ifEmpty { stringResource(id = R.string.info_dont_filled) },
        fontSize = 16.sp,
        maxLines = if (!showMoreInfo) 3 else Int.MAX_VALUE,
        overflow = if (!showMoreInfo) TextOverflow.Ellipsis else TextOverflow.Clip,
        onTextLayout = {
            textOverFlowed(it.hasVisualOverflow)
        }
    )
    if (infoOverflowed || showMoreInfo) {
        Text(
            modifier = Modifier
                .background(MaterialTheme.colorScheme.background)
                .padding(bottom = 8.dp, start = 4.dp, end = 4.dp)
                .clickable { moreInfoClick() },
            color = Color.Blue,
            text = stringResource(
                id = if (showMoreInfo)
                    R.string.roll_up
                else R.string.show_more
            ),
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FollowersRequests(
    isMyProfile: Boolean,
    followersListSize: Int,
    requestListSize: Int,
    openUserList: (type: String) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        Card(
            modifier = Modifier
                .weight(1f),
            colors = CardDefaults.cardColors(
                containerColor =
                MaterialTheme.colorScheme.primaryContainer
            ),
            onClick = { if (followersListSize != 0) openUserList(FOLLOWERS) },
        ) {
            Text(
                modifier = Modifier
                    .padding(8.dp),
                text = stringResource(
                    id = R.string.count_followers,
                    followersListSize
                ),
                textAlign = TextAlign.Center,
            )
        }
        if (isMyProfile) {
            Spacer(modifier = Modifier.width(8.dp))
            Card(
                modifier = Modifier
                    .weight(1f),
                colors = CardDefaults.cardColors(
                    containerColor =
                    MaterialTheme.colorScheme.primaryContainer
                ),
                onClick = { if (requestListSize != 0) openUserList(FRIENDSHIPS_REQUESTS) },
            ) {
                Text(
                    modifier = Modifier
                        .padding(8.dp),
                    text = stringResource(
                        id = R.string.count_requests,
                        requestListSize
                    ),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun MediaLayout(
    uuid: String,
    photoList: List<String>,
    videoList: List<String>,
    isMyProfile: Boolean,
    addMoreClicked: (type: MediaType) -> Unit
) {
    if (photoList.isNotEmpty()) MediaItemsRow(
        uuid,
        photoList,
        MediaType.IMAGE,
        isMyProfile,
        {}
    )
    if (videoList.isNotEmpty()) MediaItemsRow(
        uuid,
        videoList,
        MediaType.VIDEO,
        isMyProfile,
        {}
    )
}

@Composable
fun MediaItemsRow(
    uuid: String,
    mediaList: List<String>,
    mediaType: MediaType,
    isMyProfile: Boolean,
    openMedia: () -> Unit,
) {
    Column() {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = stringResource(
                    id = if (mediaType == MediaType.IMAGE) R.string.photo_label
                    else R.string.video_label,
                    mediaList.size.toString()
                )
            )
            Text(
                modifier = Modifier
                    .clickable(
                        onClick = openMedia
                    ),
                text = stringResource(id = R.string.see_all_label) +
                        SPACE_VALUE +
                        stringResource(
                            id = if (mediaType == MediaType.IMAGE) R.string.photo_media_label
                            else R.string.video_media_label
                        )
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
        ) {
            LazyRow(
                modifier = Modifier
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(mediaList.take(6)) { item -> MediaItem(uuid, item, mediaType) }
            }
        }
    }
}

@Composable
fun MediaItem(
    uuid: String,
    item: String,
    mediaType: MediaType,
) {
    Card(
        modifier = Modifier
            .size(50.dp),
        shape = MaterialTheme.shapes.small
    ) {
        val route =
            if (mediaType == MediaType.IMAGE) "upload_photos" else "upload_videos"
        val url = Constants.BASE_URL + "/uploads/$route/$uuid/${item}.jpeg"
        CoilImage(url)
    }
}

fun LazyListScope.friendList(
    friendList: List<Friend>,
    openProfile: (id: String) -> Unit,
    openFriendList: () -> Unit
) {
    item {
        FriendSizeText(
            friendList.isEmpty()
        )
    }
    items(friendList.take(5)) { item ->
        FriendCard(
            item,
            onProfileClick = openProfile
        )
    }
    if (friendList.size > 5)
        item { ShowMoreFriends(openFriendList) }
}

@Composable
fun FriendSizeText(
    isEmpty: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = stringResource(id = R.string.friend_list) + SPACE_VALUE +
                    if (isEmpty) {
                        stringResource(id = R.string.empty_label)
                    } else EMPTY_VALUE,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FriendCard(
    item: Friend,
    onProfileClick: (id: String) -> Unit
) {
    Card(
        modifier = Modifier
            .padding(bottom = 1.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        onClick = { onProfileClick(item.id) },
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            val url =
                Constants.BASE_URL + "/uploads/upload_photos/${item.id}/${item.avatarId}.jpeg"
            CoilImage(
                data = url,
                modifier = Modifier
                    .size(24.dp)
                    .clip(CircleShape),
                R.drawable.ic_user
            )
            Text(
                text = item.username,
                fontSize = 16.sp,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.weight(1f))
            CoilImage(
                data = if (item.onlineStatus) R.drawable.ic_user_online
                else R.drawable.ic_user_offline,
                modifier = Modifier
                    .size(24.dp),
            )
            CustomIconButton(
                modifier = Modifier
                    .size(24.dp),
                imageId = R.drawable.ic_chat_with_friend,
                onClick = {}
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShowMoreFriends(
    openFriendList: () -> Unit
) {
    Card(
        modifier = Modifier
            .padding(bottom = 5.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        onClick = { openFriendList() },
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