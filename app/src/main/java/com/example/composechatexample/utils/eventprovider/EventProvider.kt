package com.example.composechatexample.utils.eventprovider

import kotlinx.coroutines.flow.SharedFlow

interface EventProvider<T> {
    val event: SharedFlow<T>

    suspend fun emitEvent(event: T)
}