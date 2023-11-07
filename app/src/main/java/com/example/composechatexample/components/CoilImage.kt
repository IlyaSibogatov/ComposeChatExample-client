package com.example.composechatexample.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.example.composechatexample.utils.Constants

@Composable
fun CoilImage(
    data: Any?,
    modifier: Modifier = Modifier,
    placeholder: Int? = null
) {
    AsyncImage(
        modifier = modifier,
        model = ImageRequest.Builder(LocalContext.current)
            .data(data)
            .networkCachePolicy(CachePolicy.ENABLED)
            .diskCachePolicy(CachePolicy.DISABLED)
            .memoryCachePolicy(CachePolicy.ENABLED)
            .build(),
        contentDescription = Constants.CONTENT_DESCRIPTION,
        contentScale = ContentScale.Crop,
        placeholder = placeholder?.let { painterResource(id = it) },
        error = placeholder?.let { painterResource(id = it) }
    )
}