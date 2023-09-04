package com.example.composechatexample.screens.profile

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composechatexample.data.model.UserFromId
import com.example.composechatexample.data.preferences.PreferencesManager
import com.example.composechatexample.data.remote.UserService
import com.example.composechatexample.domain.model.NewUserInfo
import com.example.composechatexample.screens.profile.model.ProfileScreenEvent
import com.example.composechatexample.screens.profile.userlist.model.ProfileErrors
import com.example.composechatexample.screens.profile.userlist.model.ProfileUIState
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.ProfileDialogs
import com.example.composechatexample.utils.ResponseStatus
import com.example.composechatexample.utils.ScreenState
import com.example.composechatexample.utils.Validator
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userService: UserService,
    private val preferencesManager: PreferencesManager,
    private val validator: Validator
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState

    private val eventChannel = Channel<ProfileScreenEvent>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun getProfile(uid: String?) {
        _uiState.value = uiState.value.copy(
            uid = uid ?: preferencesManager.uuid!!,
        )
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                screenState = ScreenState.INIT
            )
            val response: UserFromId? =
                userService.getUserById(uid ?: preferencesManager.uuid!!)
            if (response != null) {
                _uiState.value = uiState.value.copy(
                    uid = response.id,
                    username = response.username,
                    selfInfo = response.selfInfo,
                    onlineStatus = response.onlineStatus,
                    lastActionTime = response.lastActionTime,
                    friends = response.friends.sortedByDescending { it.onlineStatus },
                    followers = response.followers,
                    friendshipRequests = response.friendshipRequests,
                    newInfo = NewUserInfo(
                        username = response.username,
                        selfInfo = response.selfInfo,
                    )
                )
                if (response.id == preferencesManager.uuid)
                    preferencesManager.userName = response.username
                _uiState.value = uiState.value.copy(
                    screenState = ScreenState.SUCCESS
                )
            } else {
                _uiState.value = uiState.value.copy(
                    screenState = ScreenState.ERROR,
                )
            }
        }
    }

    fun showEditDialog() {
        _uiState.value = uiState.value.copy(
            displayingView =
            if (uiState.value.displayingView != ProfileDialogs.EDIT_INFO) ProfileDialogs.EDIT_INFO
            else null
        )
        if (uiState.value.displayingView != ProfileDialogs.EDIT_INFO) {
            _uiState.value = uiState.value.copy(
                newInfo = NewUserInfo(
                    username = uiState.value.username,
                    selfInfo = uiState.value.selfInfo,
                ),
                errors = ProfileErrors()
            )
        }
    }

    fun showMoreInfo() {
        _uiState.value = uiState.value.copy(
            showMoreInfo = !uiState.value.showMoreInfo
        )
    }

    fun updateNameValue(username: String) {
        _uiState.value = uiState.value.copy(
            newInfo = NewUserInfo(
                username = username,
                selfInfo = uiState.value.newInfo.selfInfo,
            ),
            errors = ProfileErrors()
        )
    }

    fun updateInfoValue(newInfo: String) {
        _uiState.value = uiState.value.copy(
            newInfo = NewUserInfo(
                username = uiState.value.newInfo.username,
                selfInfo = newInfo,
            ),
            errors = ProfileErrors()
        )
    }

    private fun checkNewInfo(newInfo: NewUserInfo, oldInfo: String, oldUsername: String) {
        if (newInfo.selfInfo == oldInfo && newInfo.username == oldUsername) {
            _uiState.value = uiState.value.copy(
                errors = ProfileErrors(
                    userNameNotMatched = uiState.value.errors.userNameNotMatched,
                    emptyUsername = uiState.value.errors.emptyUsername,
                    newInfoNotChanged = true
                )
            )
        } else {
            validator.isValidUserName(newInfo.username).let {
                when (it) {
                    "pattern_not_matched" -> {
                        _uiState.value = uiState.value.copy(
                            errors = ProfileErrors(
                                userNameNotMatched = true,
                                emptyUsername = uiState.value.errors.emptyUsername,
                                newInfoNotChanged = uiState.value.errors.newInfoNotChanged
                            )
                        )
                    }

                    "empty_field" -> {
                        _uiState.value = uiState.value.copy(
                            errors = ProfileErrors(
                                userNameNotMatched = uiState.value.errors.userNameNotMatched,
                                emptyUsername = true,
                                newInfoNotChanged = uiState.value.errors.newInfoNotChanged
                            )
                        )
                    }

                    "username_is_ok" -> {
                        _uiState.value = uiState.value.copy(
                            errors = ProfileErrors()
                        )
                    }
                }
            }
        }
    }

    fun updateInfo() {
        checkNewInfo(uiState.value.newInfo, uiState.value.selfInfo, uiState.value.username)
        if (!uiState.value.errors.newInfoNotChanged && !uiState.value.errors.emptyUsername &&
            !uiState.value.errors.userNameNotMatched
        ) {
            viewModelScope.launch {
                val newInfo = uiState.value.newInfo
                _uiState.value = uiState.value.copy(
                    newInfo = NewUserInfo(
                        id = uiState.value.uid,
                        username = newInfo.username,
                        selfInfo = newInfo.selfInfo,
                    )
                )
                userService.updateUserInfo(uiState.value.newInfo).let {
                    if (it) {
                        showEditDialog()
                        sendEvent(
                            ProfileScreenEvent.ToastEvent(
                                msg = ResponseStatus.INFO_UPDATED.value
                            )
                        )
                        getProfile(uiState.value.uid)
                    } else {
                        sendEvent(
                            ProfileScreenEvent.ToastEvent(
                                msg = ResponseStatus.INFO_NOT_UPDATED.value
                            )
                        )
                    }
                }
            }
        }
    }

    fun setAvatar(img: ByteArray) {
        viewModelScope.launch {
            userService.setAvatar(uiState.value.uid, img)?.let {
                _uiState.value = uiState.value.copy(
                    updateImage = it.status == HttpStatusCode.OK.value
                )
            } ?: {
                _uiState.value = uiState.value.copy(
                    updateImage = false
                )
            }
            _uiState.value = uiState.value.copy(
                displayingView = null
            )
        }
    }

    fun updateImage(uri: Uri) {
        _uiState.value = uiState.value.copy(
            displayingView = ProfileDialogs.PROGRESS_LINEAR,
            updateImage = false,
            imageUri = uri
        )
    }

    fun isMyProfile(): Boolean {
        return uiState.value.uid == preferencesManager.uuid
    }

    fun isFriend(): Boolean =
        uiState.value.friends.find { it.id == preferencesManager.uuid } != null

    fun isNotInFriendRequest(): Boolean {
        return uiState.value.friendshipRequests.find { it == preferencesManager.uuid } == null
    }

    fun friendshipRequest() {
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                displayingView = ProfileDialogs.PROGRESS_LINEAR,
            )
            userService.friendshipRequest(preferencesManager.uuid!!, uiState.value.uid)?.let {
                sendEvent(
                    ProfileScreenEvent.ToastEvent(
                        when (it.msg) {
                            "Add success" -> {
                                _uiState.value = uiState.value.copy(
                                    followers = uiState.value.followers + preferencesManager.uuid!!,
                                    friendshipRequests = uiState.value.friendshipRequests + preferencesManager.uuid!!,
                                )
                                ResponseStatus.FRIENDSHIP_REQUEST_SEND.value
                            }

                            "Add failed" -> ResponseStatus.FRIENDSHIP_REQUEST_NOT_SEND.value
                            else -> ResponseStatus.ERROR.value
                        }
                    )
                )
            } ?: {
                ProfileScreenEvent.ToastEvent(ResponseStatus.ERROR.value)
            }
            _uiState.value = uiState.value.copy(
                displayingView = null
            )
        }
    }

    fun openUsersList(type: String) {
        sendEvent(
            ProfileScreenEvent.NavigateTo(
                Constants.FRIENDS_LIST_ROUTE +
                        "/${Constants.USER_UID}${uiState.value.uid}" +
                        "/${Constants.USERS_TYPE}${type}"
            )
        )
    }

    fun openProfile(uid: String) {
        if (uid == preferencesManager.uuid)
            sendEvent(ProfileScreenEvent.NavigateTo(Constants.PROFILE_ROUTE))
        else
            sendEvent(
                ProfileScreenEvent.NavigateTo(
                    "${Constants.PROFILE_ROUTE}/${Constants.USER_UID}${uid}"
                )
            )
    }

    fun selfInfoOverflowed(overflowed: Boolean) {
        _uiState.value = uiState.value.copy(
            infoOverflowed = overflowed
        )
    }

    fun showAddRemoveDialog(isFriend: Boolean? = null) {
        _uiState.value = uiState.value.copy(
            displayingView = when (isFriend) {
                true -> ProfileDialogs.REMOVE_FRIEND
                false -> ProfileDialogs.ADD_FRIEND
                else -> null
            }
        )
    }

    fun removeFriend() {
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                displayingView = ProfileDialogs.PROGRESS_LINEAR,
            )
            userService.removeFriend(preferencesManager.uuid!!, uiState.value.uid, false)?.let {
                sendEvent(
                    ProfileScreenEvent.ToastEvent(
                        when (it.msg) {
                            "friend removed" -> {
                                val friends = uiState.value.friends.toMutableList()
                                friends.remove(friends.find { it.id == preferencesManager.uuid })
                                _uiState.value = uiState.value.copy(
                                    friends = friends,
                                )
                                ResponseStatus.FRIEND_REMOVED.value
                            }

                            else -> ResponseStatus.ERROR.value
                        }
                    )
                )
            } ?: {
                ProfileScreenEvent.ToastEvent(ResponseStatus.ERROR.value)
            }
            _uiState.value = uiState.value.copy(
                displayingView = null
            )
        }
    }

    private fun sendEvent(event: ProfileScreenEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}