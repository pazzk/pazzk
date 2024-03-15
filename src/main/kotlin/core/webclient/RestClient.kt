package io.pazzk.core.webclient

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.pazzk.core.webclient.WebClient.Companion.DEFAULT_CLIENT
import io.pazzk.utils.Session
import org.slf4j.LoggerFactory

/**
 * A RestClient of *chzzk* to get session
 *
 * This class is inherited **WebClient**
 * and send Request by RESTful
 *
 * @property donationId the id of chzzk donation channel
 * @property client httpClient for request and response
 */
class RestClient(
    private val donationId: String,
    private val client: HttpClient = DEFAULT_CLIENT
) : WebClient<Session> {
    companion object {
        private const val HOST = "https://api.chzzk.naver.com/manage/v1/alerts"
    }

    private val logger = LoggerFactory.getLogger(this.javaClass)

    override suspend fun get(): Session? {
        return try {
            client.get("$HOST/donation@$donationId/session-url")
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