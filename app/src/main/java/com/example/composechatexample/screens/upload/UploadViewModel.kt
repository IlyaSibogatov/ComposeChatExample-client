package com.example.composechatexample.screens.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composechatexample.data.model.PhotoSource
import com.example.composechatexample.data.model.VideoSource
import com.example.composechatexample.data.preferences.PreferencesManager
import com.example.composechatexample.data.remote.MediaService
import com.example.composechatexample.screens.upload.model.UploadUIState
import com.example.composechatexample.utils.Constants.EMPTY_VALUE
import com.example.composechatexample.utils.Constants.ZERO_VALUE
import com.example.composechatexample.utils.MediaType
import com.example.composechatexample.utils.UploadState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UploadViewModel @Inject constructor(
    private val mediaService: MediaService,
    private val preferencesManager: PreferencesManager,
) : ViewModel() {
    private val _uiState = MutableStateFlow(UploadUIState())
    val uiState: StateFlow<UploadUIState> = _uiState

    fun setUploadType(uploadType: String) {
        _uiState.value = uiState.value.copy(
            uploadType = MediaType.valueOf(uploadType),
        )
    }

    fun updateName(value: String) {
        if (uiState.value.uploadState != UploadState.ON_LOAD) {
            if ((uiState.value.name?.length ?: ZERO_VALUE) <= NAME_MAX_VALUE &&
                value.length <= NAME_MAX_VALUE
            ) {
                _uiState.value = uiState.value.copy(
                    name = value,
                )
                if (value.isNotEmpty() && uiState.value.uploadType == MediaType.VIDEO && uiState.value.video != null) {
                    _uiState.value = uiState.value.copy(
                        uploadState = UploadState.READY_FOR_UPLOAD
                    )
                } else {
                    _uiState.value = uiState.value.copy(
                        uploadState = UploadState.NOT_READY_FOR_UPLOAD
                    )
                }
            }
        }
    }

    fun updateDescription(value: String) {
        if (uiState.value.uploadState != UploadState.ON_LOAD) {
            if ((uiState.value.description?.length ?: ZERO_VALUE) <= DESCRIPTION_MAX_VALUE &&
                value.length <= DESCRIPTION_MAX_VALUE
            ) {
                _uiState.value = uiState.value.copy(
                    description = value
                )
            }
        }
    }

    fun setPhotoSource(source: ByteArray) {
        _uiState.value = uiState.value.copy(
            image = source,
            uploadState = UploadState.READY_FOR_UPLOAD
        )
    }

    fun setVideoSource(source: VideoSource) {
        _uiState.value = uiState.value.copy(
            video = source.video,
            image = source.image,
            uploadState = if (!uiState.value.name.isNullOrEmpty()) UploadState.READY_FOR_UPLOAD
            else UploadState.NOT_READY_FOR_UPLOAD
        )
    }

    fun sendSource(uploadType: String) {
        when (uploadType) {
            MediaType.IMAGE.name -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _uiState.value = uiState.value.copy(
                        uploadState = UploadState.ON_LOAD
                    )
                    mediaService.sendPhoto(
                        preferencesManager.uuid!!,
                        PhotoSource(
                            description = uiState.value.description ?: EMPTY_VALUE,
                            image = uiState.value.image!!
                        )
                    )?.let {
                        _uiState.value = UploadUIState()
                    }
                }
            }

            MediaType.VIDEO.name -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _uiState.value = uiState.value.copy(
                        uploadState = UploadState.ON_LOAD
                    )
                    mediaService.sendVideo(
                        preferencesManager.uuid!!,
                        VideoSource(
                            name = uiState.value.name ?: EMPTY_VALUE,
                            description = uiState.value.description ?: EMPTY_VALUE,
                            video = uiState.value.video!!,
                            image = uiState.value.image!!
                        )
                    )?.let {
                        _uiState.value = UploadUIState()
                    }
                }
            }
        }
    }

    companion object {
        const val NAME_MAX_VALUE = 50
        const val DESCRIPTION_MAX_VALUE = 100
    }
}