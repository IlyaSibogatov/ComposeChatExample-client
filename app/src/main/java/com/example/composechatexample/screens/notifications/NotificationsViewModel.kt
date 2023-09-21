package com.example.composechatexample.screens.notifications

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composechatexample.data.model.NotificationType
import com.example.composechatexample.data.model.UserNotification
import com.example.composechatexample.data.preferences.PreferencesManager
import com.example.composechatexample.data.remote.UserService
import com.example.composechatexample.screens.notifications.model.NotificationsUIState
import com.example.composechatexample.utils.ScreenState
import com.example.composechatexample.utils.firebase.FirebaseService
import com.example.composechatexample.utils.firebase.NotificationEvent
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

    init {
        viewModelScope.launch {
            FirebaseService.subscribeEvent().collect { event ->
                when (event) {
                    is NotificationEvent.AddNotification -> {
                        val list = _uiState.value.notifications.toMutableList()
                        list.add(0, event.notification)
                        _uiState.value = uiState.value.copy(
                            screenState = ScreenState.SUCCESS,
                            notifications = list
                        )
                    }
                }
            }
        }
    }

    fun getNotifications() {
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                screenState = ScreenState.INIT
            )
            val result = userService.getNotifications(preferencesManager.uuid!!)
            if (result != null) {
                _uiState.value = uiState.value.copy(
                    notifications = result
                )
                _uiState.value = uiState.value.copy(
                    screenState = if (result.isNotEmpty()) ScreenState.SUCCESS else ScreenState.EMPTY_DATA
                )
            } else {
                _uiState.value = uiState.value.copy(
                    screenState = ScreenState.ERROR
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

    fun notificationScreenOpen(open: Boolean) {
        preferencesManager.notificationScreenOpen = open
    }
}