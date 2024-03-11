package io.pazzk.core.websocket

import io.pazzk.utils.Context
import kotlinx.coroutines.Job

interface WebSocketService {
    fun connect()
    suspend fun subscribe(onMessageReceived: (Context) -> Unit): Job
    fun disconnect()
}