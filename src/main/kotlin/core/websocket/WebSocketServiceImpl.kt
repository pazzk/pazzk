package io.pazzk.core.websocket

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import io.pazzk.utils.Context
import io.pazzk.utils.isInt
import io.pazzk.utils.parseToContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class WebSocketServiceImpl(
    private val url: String, private val auth: String,
    private val scope: CoroutineScope
) : WebSocketService {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val client = HttpClient(CIO) {
        install(WebSockets)
    }

    private val messageChannel = Channel<Context>(Channel.UNLIMITED)

    override fun connect() {
        scope.launch {
            connectAndReceive()
        }
    }

    private suspend fun connectAndReceive() {
        val webSocketUrl = "$url/socket.io/?auth=$auth&EIO=3&transport=websocket"
        if (logger.isDebugEnabled) {
            logger.debug("Connect to {}", webSocketUrl)
        }

        try {
            client.webSocket(request = {
                url(webSocketUrl)
            }) {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        val message = frame.readText()
                        if (message.isInt()) {
                            continue
                        }

                        messageChannel.send(parseToContext(frame.readText()))
                    }
                }
            }
            if (logger.isDebugEnabled) {
                logger.debug("Connection Success: {}", client)
            }
        } catch (e: Exception) {
            logger.error("Connection Failed", e)
        }
    }

    override suspend fun subscribe(onReceived: (Context) -> Unit): Job {
        return scope.launch {
            for (message in messageChannel) {
                onReceived(message)
            }
        }
    }

    override fun disconnect() {
        messageChannel.close()
        client.close()
    }
}