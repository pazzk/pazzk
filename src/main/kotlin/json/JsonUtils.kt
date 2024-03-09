package io.pazzk.json

data class Session(
    val code: Int,
    val message: String?,
    val content: Content
)

data class Content(
    val sessionUrl: String
)

data class Donate(
    val donationId: String,
    val animationUrl: String,
    val payAmount: Int,
    val donationText: String,
    val type: String,
    val isAnonymous: Boolean,
    val alertSoundUrl: String,
    val useSpeech: Boolean
)
