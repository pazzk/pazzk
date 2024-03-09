package io.pazzk.core.websocket

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.websocket.*
import io.ktor.client.request.*
import io.ktor.websocket.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class WebSocketServiceImpl(
    private val uri: String, private val auth: String,
    private val scope: CoroutineScope
) : WebSocketService {
    private val logger = LoggerFactory.getLogger(this.javaClass)

    private val client = HttpClient(CIO) {
        install(WebSockets)
    }

    private val messageChannel = Channel<String>(Channel.UNLIMITED)

    override fun connect() {
        scope.launch {
            connectAndReceive()
        }
    }

    private suspend fun connectAndReceive() {
        val url = "$uri/socket.io/?auth=$auth&EIO=3&transport=websocket"
        if (logger.isDebugEnabled) {
            logger.debug("Connect to {}", url)
        }

        try {
            client.webSocket(request = {
                url(url)
            }) {
                for (frame in incoming) {
                    if (frame is Frame.Text) {
                        messageChannel.send(frame.readText())
                    }
                    else if (frame is Frame.Binary) {
                        messageChannel.send(String(frame.readBytes()))
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

    override suspend fun subscribe(onMessageReceived: (String) -> Unit): Job {
        return scope.launch {
            for (message in messageChannel) {
                onMessageReceived(message)
            }
        }
    }

    override suspend fun disconnect() {
        messageChannel.close()
        client.close()
    }
}