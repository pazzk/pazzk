package io.pazzk.websocket

import kotlinx.coroutines.Job

interface WebSocketService {
    fun connect()
    suspend fun subscribe(onMessageReceived: (String) -> Unit): Job
    suspend fun disconnect()
}
