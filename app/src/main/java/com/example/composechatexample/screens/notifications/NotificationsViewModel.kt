package com.example.composechatexample.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composechatexample.data.model.NotificationType
import com.example.composechatexample.data.model.UserNotification
import com.example.composechatexample.data.preferences.PreferencesManager
import com.example.composechatexample.data.remote.UserService
import com.example.composechatexample.screens.notifications.model.NotificationsUIState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotificationsViewModel @Inject constructor(
    private val userService: UserService,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(NotificationsUIState())
    val uiState: StateFlow<NotificationsUIState> = _uiState

    fun getNotifications() {
        viewModelScope.launch {
            userService.getNotifications(preferencesManager.uuid!!)?.let {
                _uiState.value = uiState.value.copy(
                    notifications = it
                )
            }
        }
    }

    fun friendshipAction(item: UserNotification, accept: Boolean = false) {
        viewModelScope.launch {
            userService.friendshipAccept(preferencesManager.uuid!!, item.senderId, accept)?.let {
                if (it.status == HttpStatusCode.OK.value) {
                    val list = _uiState.value.notifications.toMutableList()
                    val index = _uiState.value.notifications.indexOf(item)
                    list[index] = list[index].copy(
                        type = if (accept) NotificationType.ACCEPTED_FRIENDSHIP
                        else NotificationType.DECLINED_FRIENDSHIP
                    )
                    _uiState.value = uiState.value.copy(
                        notifications = list
                    )
                }
            }
        }
    }
}