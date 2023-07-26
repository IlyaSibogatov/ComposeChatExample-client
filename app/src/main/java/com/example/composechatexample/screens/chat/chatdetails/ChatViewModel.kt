package com.example.composechatexample.screens.chat.chatdetails

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composechatexample.data.preferences.PreferencesManager
import com.example.composechatexample.data.remote.ChatSocketService
import com.example.composechatexample.data.remote.MessageService
import com.example.composechatexample.domain.model.Message
import com.example.composechatexample.domain.model.SendType
import com.example.composechatexample.screens.chat.chatdetails.model.ChatScreenEvent
import com.example.composechatexample.screens.chat.chatdetails.model.ChatUIState
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.Resources
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val messageService: MessageService,
    private val socketService: ChatSocketService,
    private val preferencesManager: PreferencesManager
) : ViewModel() {
    private val _uiState = MutableStateFlow(ChatUIState())
    val uiState: StateFlow<ChatUIState> = _uiState

    private val eventChannel = Channel<ChatScreenEvent>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    fun updateChatId(id: String) {
        _uiState.value = uiState.value.copy(
            chatId = id.replace("chat_id", "")
        )
    }

    @RequiresApi(Build.VERSION_CODES.N)
    fun connectToChat() {
        _uiState.value = uiState.value.copy(
            username = preferencesManager.userName,
            isLoading = true,
        )
        viewModelScope.launch {
            when (val result =
                socketService.initSession(preferencesManager.userName, uiState.value.chatId)) {
                is Resources.Success -> {
                    socketService.observeMessages(uiState.value.username)
                        .onEach { message ->
                            val newList = uiState.value.messages.toMutableList()
                            when {
                                message.message.startsWith(REMOVE_MESSAGE_ROUTE) -> {
                                    newList.removeIf {
                                        it.id == message.message.replace(
                                            REMOVE_MESSAGE_ROUTE, EMPTY_CHAR
                                        )
                                    }
                                }

                                message.message.startsWith(EDIT_MESSAGE_ROUTE) -> {
                                    val splittedMessage = message.message.split(SPLITTER)
                                    val id =
                                        splittedMessage[0].replace(EDIT_MESSAGE_ROUTE, EMPTY_CHAR)
                                    val msg = splittedMessage[1].trim()
                                    newList.find { it.id == id }?.let {
                                        newList.set(
                                            index = newList.indexOf(it),
                                            Message(
                                                id = it.id,
                                                message = msg,
                                                username = it.username,
                                                myMessage = it.myMessage,
                                                wasEdit = it.message != msg,
                                                formattedTime = it.formattedTime,
                                            )
                                        )
                                    }
                                }

                                else -> {
                                    newList.add(0, message)
                                }
                            }
                            _uiState.value = uiState.value.copy(
                                messages = newList
                            )
                        }.launchIn(viewModelScope)
                }

                is Resources.Error -> {
                    sendEvent(ChatScreenEvent.ToastEvent(result.message ?: "Unknown error"))
                }
            }
            _uiState.value = uiState.value.copy(
                isLoading = false,
            )
            getAllMessages()
        }
    }

    fun updateTypedMessage(message: String) {
        _uiState.value = uiState.value.copy(
            message = message
        )
    }

    private fun getAllMessages() {
        viewModelScope.launch {
            _uiState.value = uiState.value.copy(
                isLoading = true,
            )
            val result = messageService.getAllMessages(uiState.value.chatId, uiState.value.username)
            _uiState.value = uiState.value.copy(
                messages = result,
                isLoading = false,
            )
        }
    }

    fun prepareEdit(msg: String) {
        updateTypedMessage(msg)
        editSelect(true)
    }

    fun updateSelectedItem(id: String) {
        _uiState.value = uiState.value.copy(
            selectedMsgId = id
        )
    }

    fun editSelect(selected: Boolean) {
        _uiState.value = uiState.value.copy(
            editSelect = selected
        )
        if (!selected) {
            updateTypedMessage("")
        }
    }

    fun sendMessage(type: SendType) {
        _uiState.value = uiState.value.copy(
            onSending = true
        )
        viewModelScope.launch {
            when (type) {
                SendType.SEND -> {
                    if (uiState.value.message.isNotBlank()) {
                        if (uiState.value.editSelect) {
                            socketService.sendMessage(
                                msg = EDIT_MESSAGE_ROUTE + uiState.value.selectedMsgId +
                                        SPLITTER + uiState.value.message.trim()
                            )
                        } else {
                            socketService.sendMessage(uiState.value.message.trim())
                        }
                    }
                }

                SendType.REMOVE -> {
                    socketService.sendMessage(REMOVE_MESSAGE_ROUTE + uiState.value.selectedMsgId)
                }
                else -> {}
            }
            updateTypedMessage("")
            _uiState.value = uiState.value.copy(
                selectedMsgId = "",
                editSelect = false,
                onSending = false
            )
        }
    }

    fun socketDisconnect() {
        viewModelScope.launch {
            socketService.closeSessions()
        }
    }

    fun navigateToProfile() {
        sendEvent(ChatScreenEvent.NavigateTo(Constants.PROFILE_ROUTE))
    }

    private fun sendEvent(event: ChatScreenEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }

    override fun onCleared() {
        super.onCleared()
        socketDisconnect()
    }

    companion object {
        const val EDIT_MESSAGE_ROUTE = "update_message_with_id="
        const val REMOVE_MESSAGE_ROUTE = "remove_message_with_id="
        const val SPLITTER = "/"
        const val EMPTY_CHAR = ""
    }
}