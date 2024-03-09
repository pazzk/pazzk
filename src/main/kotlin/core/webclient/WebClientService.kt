package io.pazzk.core.webclient

interface WebClientService<T> {

    suspend fun get(): T?

    fun close()

}