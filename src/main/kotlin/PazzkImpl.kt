package io.pazzk

import io.pazzk.core.webclient.WebClientService
import io.pazzk.core.websocket.WebSocketService
import io.pazzk.core.websocket.WebSocketServiceImpl
import io.pazzk.json.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class PazzkImpl(
    private val webClient: WebClientService<Session>,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())
) : Pazzk {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private lateinit var webSocket: WebSocketService
    private lateinit var webSocketScope: CoroutineScope
    private val subscribers = ArrayList<(Any) -> Unit>()

    override fun connect(useParentScope: Boolean) {
        try {
            scope.launch {
                val response = webClient.get()
                if (response?.code != 200) {
                    throw IllegalStateException(
                        "DonationId is not support in chzzk!"
                    )
                }

                val sessionUrl = response.content.sessionUrl
                val (webSocketUrl, auth) = convertWebSocketUrl(sessionUrl)
                logger.info("websocket url: {}", webSocketUrl)

                webSocketScope = if (useParentScope) scope else CoroutineScope(scope.coroutineContext + Job())
                webSocket = WebSocketServiceImpl(webSocketUrl, auth, webSocketScope)
                webSocket.connect()
                webSocket.subscribe {
                    for (subscriber in subscribers) {
                        subscriber(it)
                    }
                }
            }
        } catch (e: IllegalStateException) {
            logger.error("Error occurred", e)
        }
    }

    private fun convertWebSocketUrl(sessionUrl: String): Pair<String, String> {
        if (sessionUrl.startsWith("https")) {
            val websocketUrl = sessionUrl.replace("https", "wss")
                .substring(0, sessionUrl.lastIndexOf(":") - 2)
            return Pair(websocketUrl, getParamFrom(sessionUrl, "auth")!!)
        }
        throw IllegalArgumentException("$sessionUrl is not https url")
    }

    private fun getParamFrom(url: String, paramName: String): String? {
        val regex = """[?&]$paramName=([^&]+)""".toRegex()
        val matchResult = regex.find(url)
        return matchResult?.groups?.get(1)?.value
    }

    override fun addListener(callback: (response: Any) -> Unit) {
        subscribers.add(callback)
    }

    override fun disconnect() {
        webClient.close()
        webSocket.disconnect()
    }
}