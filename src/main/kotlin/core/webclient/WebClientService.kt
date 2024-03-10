package io.pazzk.core.webclient

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.jackson.*

interface WebClientService<T> {

    suspend fun get(): T?

    fun close()

    companion object {
        val DEFAULT_CLIENT = HttpClient(CIO) {
            install(ContentNegotiation) {
                jackson()
            }
        }
    }

}