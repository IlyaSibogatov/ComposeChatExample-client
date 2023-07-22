package com.example.composechatexample.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composechatexample.data.preferences.PreferencesManager
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
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState

    private val eventChannel = Channel<ProfileScreenEvent>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    init {
        _uiState.value = uiState.value.copy(
            username = preferencesManager.userName
        )
    }

    fun changeName(name: String) {
        _uiState.value = uiState.value.copy(
            name = name
        )
    }

    fun changeNumber(number: String) {
        _uiState.value = uiState.value.copy(
            number = number
        )
    }

    fun changeEmail(email: String) {
        _uiState.value = uiState.value.copy(
            email = email
        )
    }

    fun allowCorrection() {
        _uiState.value = uiState.value.copy(
            canEditProfile = !uiState.value.canEditProfile
        )
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