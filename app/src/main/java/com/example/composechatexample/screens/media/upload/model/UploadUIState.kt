package com.example.composechatexample.screens.media.upload.model

import com.example.composechatexample.utils.MediaType
import com.example.composechatexample.utils.UploadState

data class UploadUIState(
    var name: String? = null,
    var uploadType: MediaType? = null,
    var description: String? = null,
    var video: ByteArray? = null,
    var image: ByteArray? = null,
    var uploadState: UploadState = UploadState.NOT_READY_FOR_UPLOAD
)
