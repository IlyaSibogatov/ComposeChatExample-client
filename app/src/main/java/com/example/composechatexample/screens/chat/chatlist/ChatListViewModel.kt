package com.example.composechatexample.screens.chat.chatlist

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composechatexample.data.preferences.PreferencesManager
import com.example.composechatexample.data.remote.MessageService
import com.example.composechatexample.data.remote.UserService
import com.example.composechatexample.data.response.DefaultResponse
import com.example.composechatexample.domain.model.Chat
import com.example.composechatexample.domain.model.NewChat
import com.example.composechatexample.screens.chat.chatlist.model.ChatListErrors
import com.example.composechatexample.screens.chat.chatlist.model.ChatListScreenEvent
import com.example.composechatexample.screens.chat.chatlist.model.ChatListUIState
import com.example.composechatexample.screens.chat.chatlist.model.CreatedChat
import com.example.composechatexample.screens.chat.chatlist.model.DisplayDialog
import com.example.composechatexample.utils.Constants
import com.example.composechatexample.utils.ResponseStatus
import com.example.composechatexample.utils.ScreenState
import dagger.hilt.android.lifecycle.HiltViewModel
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val preferencesManager: PreferencesManager,
    private val messageService: MessageService,
    private val userService: UserService,
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatListUIState())
    val uiState: StateFlow<ChatListUIState> = _uiState

    private val eventChannel = Channel<ChatListScreenEvent>(Channel.BUFFERED)
    val eventsFlow = eventChannel.receiveAsFlow()

    init {
        _uiState.value = uiState.value.copy(
            userLogged = preferencesManager.userLogged,
            username = preferencesManager.userName
        )
        println("TOKEN LENGTH ---> ${preferencesManager.tokenFcm!!.length}")
    }

    fun updateToken() {
        with(preferencesManager) {
            viewModelScope.launch {
                userService.updateToken(uuid!!, tokenFcm!!, deviceId!!, deviceType!!)
            }
        }
    }

    fun loadChatList() {
        _uiState.value = uiState.value.copy(
            screenState = ScreenState.INIT
        )
        viewModelScope.launch {
            val result = messageService.getAllChats()?.toMutableList()
            if (result != null) {
                _uiState.value = uiState.value.copy(
                    chats = result,
                    newChats = result,
                    screenState = ScreenState.SUCCESS
                )
            } else {
                _uiState.value = uiState.value.copy(
                    chats = mutableListOf(),
                    newChats = mutableListOf(),
                    screenState = ScreenState.ERROR
                )
            }
        }
    }

    fun checkChat(item: Chat): Boolean {
        _uiState.value = uiState.value.copy(
            chatInfo = item
        )
        if (uiState.value.chatInfo!!.password.isBlank() ||
            preferencesManager.uuid == uiState.value.chatInfo!!.ownerId
        )
            navigateToRoom()
        else _uiState.value = uiState.value.copy(
            dialogs = DisplayDialog(
                passDialog = true
            )
        )
        return item.owner == uiState.value.username || item.password.isBlank()
    }

    fun deleteChat() {
        uiState.value.chatInfo?.id?.let {
            viewModelScope.launch {
                messageService.deleteChat(it).let {
                    when (it) {
                        true -> {
                            loadChatList()
                        }

                        false -> {
                            sendEvent(ChatListScreenEvent.ToastEvent(ResponseStatus.ERROR.value))
                        }
                    }
                }
            }
        }
    }

    fun chatLongClick(item: Chat): Boolean? {
        return if (item.ownerId == preferencesManager.uuid) {
            _uiState.value = uiState.value.copy(
                chatInfo = item,
            )
            true
        } else null
    }

    fun showAlertDialog(chat: Chat?) {
        _uiState.value = uiState.value.copy(
            chatInfo = chat,
            dialogs = DisplayDialog(
                passDialog = !uiState.value.dialogs.passDialog,
            ),
            roomPassword = "",
            errors = ChatListErrors(
                emptyChatPass =
                if (uiState.value.dialogs.passDialog)
                    uiState.value.errors.entryPasswordError
                else false
            )
        )
    }

    fun checkPassword() {
        if (uiState.value.roomPassword == uiState.value.chatInfo?.password)
            navigateToRoom()
        else {
            _uiState.value = uiState.value.copy(
                errors = ChatListErrors(
                    entryPasswordError = true,
                )
            )
        }
    }

    fun updatePassword(newPass: String) {
        _uiState.value = uiState.value.copy(
            errors = ChatListErrors(
                entryPasswordError = false,
            ),
            roomPassword = newPass
        )
    }

    private fun navigateToRoom() {
        clearDialog()
        sendEvent(
            ChatListScreenEvent.NavigateTo(
                "${Constants.CHAT_ROUTE}/${Constants.CHAT_ID}${uiState.value.chatInfo!!.id}"
            )
        )
    }

    private fun clearDialog() {
        _uiState.value = uiState.value.copy(
            dialogs = DisplayDialog(
                passDialog = false
            ),
            roomPassword = ""
        )
    }

    fun showCreateDialog(editChat: Boolean = false) {
        _uiState.value = uiState.value.copy(
            updateChat = editChat,
            dialogs = DisplayDialog(
                createDialog = !uiState.value.dialogs.createDialog
            )
        )
        if (!uiState.value.dialogs.createDialog) {
            _uiState.value = uiState.value.copy(
                errors = ChatListErrors(),
                createdChat = CreatedChat(),
            )
        }
        if (editChat) {
            _uiState.value = uiState.value.copy(
                createdChat = CreatedChat(
                    chatName = uiState.value.chatInfo!!.name,
                    passEnable = uiState.value.chatInfo!!.password.isNotEmpty(),
                    chatPass = uiState.value.chatInfo!!.password
                )
            )
        }
    }

    fun switchPass() {
        _uiState.value = uiState.value.copy(
            createdChat = CreatedChat(
                chatName = uiState.value.createdChat.chatName,
                chatPass = uiState.value.createdChat.chatPass,
                passEnable = !uiState.value.createdChat.passEnable
            ),
        )
        if (uiState.value.createdChat.passEnable && uiState.value.errors.emptyChatPass) {
            _uiState.value = uiState.value.copy(
                errors = ChatListErrors(
                    emptyChatName = uiState.value.errors.emptyChatName,
                    emptyChatPass = false
                )
            )
        }
    }

    fun updateOwnChatName(newText: String) {
        _uiState.value = uiState.value.copy(
            createdChat = CreatedChat(
                chatName = newText,
                chatPass = uiState.value.createdChat.chatPass,
                passEnable = uiState.value.createdChat.passEnable
            ),
            errors = ChatListErrors(
                emptyChatName = false,
                emptyChatPass = uiState.value.errors.emptyChatPass,
            )
        )
    }

    fun updateOwnChatPass(newText: String) {
        _uiState.value = uiState.value.copy(
            createdChat = CreatedChat(
                chatName = uiState.value.createdChat.chatName,
                chatPass = newText,
                passEnable = uiState.value.createdChat.passEnable
            ),
            errors = ChatListErrors(
                emptyChatName = uiState.value.errors.emptyChatName,
                emptyChatPass = false,
            )
        )
    }

    fun createChat() {
        if (
            uiState.value.createdChat.chatName.isBlank() ||
            uiState.value.createdChat.passEnable && uiState.value.createdChat.chatPass.isBlank()
        ) {
            _uiState.value = uiState.value.copy(
                errors = ChatListErrors(
                    emptyChatName = uiState.value.createdChat.chatName.isBlank(),
                    emptyChatPass = uiState.value.createdChat.passEnable &&
                            uiState.value.createdChat.chatPass.isBlank()
                )
            )
        } else {
            viewModelScope.launch {
                if (uiState.value.updateChat) {
                    messageService.updateChat(
                        NewChat(
                            name = uiState.value.createdChat.chatName,
                            password = if (
                                uiState.value.createdChat.passEnable
                            ) uiState.value.createdChat.chatPass
                            else "",
                            owner = uiState.value.username,
                            ownerId = preferencesManager.uuid!!,
                        )
                    )?.let {
                        createOrEditSuccess(it)
                    } ?: sendEvent(ChatListScreenEvent.ToastEvent(ResponseStatus.ERROR.value))
                } else {
                    messageService.createChat(
                        NewChat(
                            name = uiState.value.createdChat.chatName,
                            password = if (
                                uiState.value.createdChat.passEnable
                            ) uiState.value.createdChat.chatPass
                            else "",
                            owner = uiState.value.username,
                            ownerId = preferencesManager.uuid!!,
                        )
                    )?.let {
                        createOrEditSuccess(it)
                    } ?: sendEvent(ChatListScreenEvent.ToastEvent(ResponseStatus.ERROR.value))
                }
            }
        }
    }

    private fun createOrEditSuccess(response: DefaultResponse) {
        when (response.status) {
            HttpStatusCode.OK.value -> {
                showCreateDialog()
                loadChatList()
            }
        }
        sendEvent(ChatListScreenEvent.ToastEvent(response.msg))
    }

    fun updateSearchQuery(search: String) {
        _uiState.value = uiState.value.copy(
            searchQuery = search,
            newChats = uiState.value.chats.filter {
                it.name.contains(search)
            }
        )
    }

    private fun sendEvent(event: ChatListScreenEvent) {
        viewModelScope.launch {
            eventChannel.send(event)
        }
    }
}