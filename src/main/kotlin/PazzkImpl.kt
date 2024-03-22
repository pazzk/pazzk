package io.pazzk

import io.pazzk.core.webclient.WebClient
import io.pazzk.core.websocket.WebSocketService
import io.pazzk.core.websocket.WebSocketServiceImpl
import io.pazzk.utils.Context
import io.pazzk.utils.Session
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.slf4j.LoggerFactory

class PazzkImpl(
    private val webClient: WebClient<Session>,
    private val scope: CoroutineScope = CoroutineScope(Dispatchers.IO + Job())
) : Pazzk {
    private val logger = LoggerFactory.getLogger(this.javaClass)
    private lateinit var webSocket: WebSocketService
    private lateinit var webSocketScope: CoroutineScope
    private val subscribers = ArrayList<(Context) -> Unit>()

    override fun connect(useParentScope: Boolean) {
        try {
            scope.launch {
                val response = webClient.get()
                // response code must be 200.
                if (response?.code != 200) {
                    throw IllegalStateException(
                        "DonationId is not support in chzzk!"
                    )
                }

                val sessionUrl = response.content.sessionUrl
                // parse websocket url and auth from session url
                val (webSocketUrl, auth) = convertWebSocketUrl(sessionUrl)
                if (logger.isDebugEnabled) {
                    logger.debug("websocket url: {}", webSocketUrl)
                }

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
            return Pair(websocketUrl, getParamFrom(sessionUrl)!!)
        }
        throw IllegalArgumentException("$sessionUrl is not https url")
    }

    private fun getParamFrom(url: String): String? {
        val regex = """[?&]auth=([^&]+)""".toRegex()
        val matchResult = regex.find(url)
        return matchResult?.groups?.get(1)?.value
    }

    override fun addListener(callback: (response: Context) -> Unit) {
        subscribers.add(callback)
    }

    override fun disconnect() {
        webClient.close()
        webSocket.disconnect()
    }
}