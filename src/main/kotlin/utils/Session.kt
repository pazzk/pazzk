package io.pazzk.utils

data class Session(
    val code: Int,
    val message: String?,
    val content: Content
)

data class Content(
    val sessionUrl: String
)
