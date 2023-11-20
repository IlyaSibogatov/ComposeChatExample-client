package com.example.composechatexample.screens.media.upload

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.navigation.NavHostController
import com.example.composechatexample.R
import com.example.composechatexample.components.ActionButton
import com.example.composechatexample.components.CoilImage
import com.example.composechatexample.components.CustomIconButton
import com.example.composechatexample.screens.dialogs.ClearFieldIcon
import com.example.composechatexample.utils.Constants.EMPTY_VALUE
import com.example.composechatexample.utils.Constants.ZERO_VALUE
import com.example.composechatexample.utils.Ext
import com.example.composechatexample.utils.MediaType
import com.example.composechatexample.utils.UploadState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UploadScreen(
    navController: NavHostController,
    uploadType: String? = null,
) {
    val context = LocalContext.current
    val viewModel: UploadViewModel = hiltViewModel()
    val uiState = viewModel.uiState.collectAsState()

    val imagePicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.setPhotoSource(Ext.getCompressedImage(it, context))
            }
        }
    }

    val videoPicker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.setVideoSource(Ext.getVideoStream(context, it))
            }
        }
    }

    ConstraintLayout(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
    ) {
        val (
            arrowBack, videoNameLength, videoName, descriptionLength,
            descriptionText, displayedImage, actionButton,
        ) = createRefs()

        CustomIconButton(
            modifier = Modifier
                .constrainAs(arrowBack) {
                    top.linkTo(parent.top)
                    start.linkTo(parent.start)
                },
            imageId = R.drawable.ic_arrow_back,
            color = MaterialTheme.colorScheme.primary,
            onClick = { navController.popBackStack() }
        )

        if (uploadType == MediaType.VIDEO.name) {
            //Video name length (max50)
            Text(
                modifier = Modifier
                    .constrainAs(videoNameLength) {
                        top.linkTo(arrowBack.bottom)
                        end.linkTo(parent.end)
                    },
                text = stringResource(
                    id = R.string.upload_name_count,
                    uiState.value.name?.length ?: ZERO_VALUE.toString()
                )
            )
            //Video name
            OutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .constrainAs(videoName) {
                        top.linkTo(videoNameLength.bottom)
                        start.linkTo(parent.start)
                        end.linkTo(parent.end)
                    },
                value = uiState.value.name ?: EMPTY_VALUE,
                onValueChange = viewModel::updateName,
                placeholder = {
                    Text(
                        text = stringResource(id = R.string.upload_video_name)
                    )
                },
                shape = MaterialTheme.shapes.small,
                maxLines = 3,
                trailingIcon = {
                    ClearFieldIcon(
                        onClick = { viewModel.updateName(EMPTY_VALUE) }
                    )
                },
                isError = false,
                supportingText = {}
            )
        }

        //Upload description length (max100)
        Text(
            modifier = Modifier
                .padding(top = 12.dp)
                .constrainAs(descriptionLength) {
                    top.linkTo(
                        if (uploadType == MediaType.VIDEO.name) videoName.bottom
                        else arrowBack.bottom
                    )
                    end.linkTo(parent.end)
                },
            text = stringResource(
                id = R.string.upload_description_count,
                uiState.value.description?.length ?: ZERO_VALUE.toString()
            )
        )

        //Upload description
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth()
                .constrainAs(descriptionText) {
                    top.linkTo(descriptionLength.bottom)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            value = uiState.value.description ?: EMPTY_VALUE,
            onValueChange = viewModel::updateDescription,
            placeholder = {
                Text(
                    text = stringResource(id = R.string.upload_description)
                )
            },
            shape = MaterialTheme.shapes.small,
            maxLines = 5,
            trailingIcon = {
                ClearFieldIcon(
                    onClick = { viewModel.updateDescription(EMPTY_VALUE) }
                )
            },
            isError = false,
            supportingText = {}
        )

        //Display image
        CoilImage(
            data = uiState.value.image,
            modifier = Modifier
                .padding(top = 12.dp)
                .clip(MaterialTheme.shapes.small)
                .clickable(
                    onClick = {
                        when (uploadType) {
                            MediaType.VIDEO.name -> videoPicker.launch("video/*")
                            MediaType.IMAGE.name -> imagePicker.launch("image/*")
                        }
                    }
                )
                .size(256.dp)
                .border(BorderStroke(2.dp, MaterialTheme.colorScheme.primaryContainer))
                .constrainAs(displayedImage) {
                    top.linkTo(descriptionText.bottom)
                    bottom.linkTo(actionButton.top)
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                },
            placeholder = R.drawable.ic_upload_files
        )

        //Action button
        ActionButton(
            modifier = Modifier
                .alpha(if (uiState.value.uploadState == UploadState.READY_FOR_UPLOAD) 1f else 0.5f)
                .constrainAs(actionButton) {
                    start.linkTo(parent.start)
                    end.linkTo(parent.end)
                    bottom.linkTo(parent.bottom)
                },
            text = stringResource(
                id = if (uploadType == MediaType.VIDEO.name) R.string.upload_video
                else R.string.upload_image
            ),
            onClick = {
                if (uiState.value.uploadState == UploadState.READY_FOR_UPLOAD) uploadType?.let {
                    viewModel.sendSource(it)
                }
            }
        )
    }
    val lifeCycleOwner = LocalLifecycleOwner.current
    DisposableEffect(key1 = lifeCycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_START)
                viewModel.setUploadType(uploadType!!)
        }
        lifeCycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifeCycleOwner.lifecycle.removeObserver(observer)
        }
    }
}