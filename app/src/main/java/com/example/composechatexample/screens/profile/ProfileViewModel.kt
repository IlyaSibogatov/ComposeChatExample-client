package com.example.composechatexample.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composechatexample.data.preferences.PreferencesManager
import com.example.composechatexample.data.remote.UserService
import com.example.composechatexample.domain.model.User
import com.example.composechatexample.screens.profile.model.ProfileScreenEvent
import com.example.composechatexample.utils.Constants
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
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                loadingStatus = true
            )
            val response: User? =
                userService.getUserById(uid ?: preferencesManager.uuid!!)
            if (response != null) {
                _uiState.value = uiState.value.copy(
                    uid = response.id,
                    username = response.username,
                    selfInfo = response.selfInfo,
                    onlineStatus = response.onlineStatus,
                    lastActionTime = response.lastActionTime,
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

    fun openFriendList() {
        sendEvent(ProfileScreenEvent.NavigateTo(Constants.FRIENDS_LIST_ROUTE))
    }

    private fun sendEvent(event: ProfileScreenEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}