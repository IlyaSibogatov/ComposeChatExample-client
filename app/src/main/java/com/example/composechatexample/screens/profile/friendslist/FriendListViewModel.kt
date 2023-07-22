package com.example.composechatexample.screens.profile.friendslist

import androidx.lifecycle.ViewModel
import com.example.composechatexample.data.preferences.PreferencesManager
import com.example.composechatexample.screens.profile.ProfileUIState
import com.example.composechatexample.screens.profile.model.ProfileScreenEvent
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import javax.inject.Inject

@HiltViewModel
class FriendListViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager
): ViewModel() {
    private val _uiState = MutableStateFlow(ProfileUIState())
    val uiState: StateFlow<ProfileUIState> = _uiState

    private val eventChannel = Channel<ProfileScreenEvent>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()
}