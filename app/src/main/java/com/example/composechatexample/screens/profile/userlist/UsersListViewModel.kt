package com.example.composechatexample.screens.profile.userlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composechatexample.data.preferences.PreferencesManager
import com.example.composechatexample.data.remote.UserService
import com.example.composechatexample.screens.profile.userlist.model.FriendListUIState
import com.example.composechatexample.screens.profile.userlist.model.UsersListEvent
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.Constants.FRIENDS
import com.example.composechatexample.utils.Constants.POP_BACK_STACK
import com.example.composechatexample.utils.ResponseStatus
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UsersListViewModel @Inject constructor(
    private val userService: UserService,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(FriendListUIState())
    val uiState: StateFlow<FriendListUIState> = _uiState

    private val eventChannel = Channel<UsersListEvent>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun getFollowerFriends(uid: String?, type: String?) {
        _uiState.value = uiState.value.copy(
            uid = uid ?: preferencesManager.uuid!!,
            screenType = type ?: FRIENDS,
            loadingStatus = true,
        )
        viewModelScope.launch {
            userService.getFollowerFriends(uiState.value.uid, uiState.value.screenType)?.let {
                _uiState.value = uiState.value.copy(
                    usersList = it,
                    loadingStatus = false,
                )
            }
        }
    }

    fun friendshipsAccept(userId: String, accept: Boolean) {
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                loadingStatus = true
            )
            userService.friendshipAccept(preferencesManager.uuid!!, userId, accept)?.let {
                if (it.msg == "Action success") {
                    val list = uiState.value.usersList.toMutableList()
                    list.remove(list.find { user -> user.id == userId })
                    _uiState.value = uiState.value.copy(
                        usersList = list
                    )
                }
                _uiState.value = uiState.value.copy(
                    loadingStatus = false
                )
                if (uiState.value.usersList.isEmpty()) {
                    sendEvent(UsersListEvent.NavigateTo(POP_BACK_STACK))
                }
            } ?: sendEvent(UsersListEvent.ToastEvent(ResponseStatus.ERROR.value))
        }
    }

    fun openProfile(uid: String) {
        if (uid == preferencesManager.uuid)
            sendEvent(UsersListEvent.NavigateTo(Constants.PROFILE_ROUTE))
        else
            sendEvent(
                UsersListEvent.NavigateTo(
                    "${Constants.PROFILE_ROUTE}/${Constants.USER_UID}${uid}"
                )
            )
    }

    private fun sendEvent(event: UsersListEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}