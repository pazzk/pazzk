package io.pazzk.core.webclient

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.pazzk.json.Session
import org.slf4j.LoggerFactory

class RestClient(
    private val donationId: String,
    private val client: HttpClient
) : WebClientService<Session> {
    companion object {
        private const val host = "https://api.chzzk.naver.com/manage/v1/alerts"
    }

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override suspend fun get(): Session? {
        return try {
            client.get("$host/donation@$donationId/session-url")
                .body<Session>()
        } catch (e: Exception) {
            logger.error("Error Occurred", e)
            null
        }
    }

    override fun close() {
        client.close()
    }
}