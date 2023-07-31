package com.example.composechatexample.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composechatexample.data.model.UserFromId
import com.example.composechatexample.data.preferences.PreferencesManager
import com.example.composechatexample.data.remote.UserService
import com.example.composechatexample.screens.profile.model.ProfileScreenEvent
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.Constants.ERROR
import com.example.composechatexample.utils.Constants.FAILED
import com.example.composechatexample.utils.Constants.SUCCESS
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val userService: UserService,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState

    private val eventChannel = Channel<ProfileScreenEvent>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun getProfile(uid: String?) {
        uid?.let {
            _uiState.value = uiState.value.copy(
                uid = it,
            )
        }
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                loadingStatus = true
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
                    gettingUserError = false,
                )
            } else {
                _uiState.value = uiState.value.copy(
                    gettingUserError = true,
                )
            }
            _uiState.value = uiState.value.copy(
                loadingStatus = false
            )
        }
    }

    fun isMyProfile(): Boolean {
        return uiState.value.uid == preferencesManager.uuid
    }

    fun isFriend(): Boolean {
        return uiState.value.friends.find {
            it.id == preferencesManager.uuid
        } != null || uiState.value.uid == preferencesManager.uuid
    }

    fun friendshipRequest() {
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                loadingStatus = true
            )
            userService.friendshipRequest(preferencesManager.uuid!!, uiState.value.uid).let {
                sendEvent(
                    ProfileScreenEvent.ToastEvent(
                        when (it) {
                            true -> SUCCESS
                            false -> FAILED
                            null -> ERROR
                        }
                    )
                )
            }
            _uiState.value = uiState.value.copy(
                loadingStatus = false
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

    private fun sendEvent(event: ProfileScreenEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}