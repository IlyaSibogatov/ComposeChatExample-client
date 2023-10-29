package com.example.composechatexample.screens.upload.model

import com.example.composechatexample.utils.UploadState
import com.example.composechatexample.utils.UploadType

data class UploadUIState(
    var name: String? = null,
    var uploadType: UploadType? = null,
    var description: String? = null,
    var video: ByteArray? = null,
    var image: ByteArray? = null,
    var uploadState: UploadState = UploadState.NOT_READY_FOR_UPLOAD
)
