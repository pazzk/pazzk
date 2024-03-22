package io.pazzk.core.websocket

import io.pazzk.utils.Context
import kotlinx.coroutines.Job

/**
 * A Websocket Service for subscribe chzzk donation
 *
 * When using the implementation, be sure to call it in the following order: connect -> subscribe -> disconnect.
 */
interface WebSocketService {

    /**
     * Assign resources and connect to specific url.
     * The specific url is defining from implementation
     */
    fun connect()

    /**
     * Subscribe the context from websocket.
     * Which also returns Job from coroutine, available to cancel or check status.
     *
     * @param onReceived define non-returnable implementation which provide with context
     * @return coroutine job
     */
    suspend fun subscribe(onReceived: (Context) -> Unit): Job

    /**
     * Release resources and disconnect(close) websocket.
     */
    fun disconnect()
}