package io.pazzk.core.webclient

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*

/**
 * A Web Client of *chzzk*
 *
 * This interface only provides the suspendable **HTTP GET** method.
 *
 * Note:
 *      Provides a CIO-based client by default for its child classes.
 *
 * @param T the type of response that response from GET
 * @see io.pazzk.core.webclient.RestClient
 */
interface WebClient<T> {

    /**
     * Request GET and Response by suspendable
     *
     * @return response type
     * @sample io.pazzk.core.webclient.RestClient.get
     */
    suspend fun get(): T?

    /**
     * Close resources and client
     *
     * Never re-open until assign new client again
     */
    fun close()

    companion object {
        val DEFAULT_CLIENT = HttpClient(CIO) {
            install(ContentNegotiation) {
                jackson()
            }
        }
    }

}