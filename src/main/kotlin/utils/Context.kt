package io.pazzk.utils

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.util.regex.Pattern

sealed interface Context

data class Donate(
    val donationId: String,
    val animationUrl: String,
    val payAmount: Int,
    val donationText: String,
    val type: String,
    val isAnonymous: Boolean,
    val alertSoundUrl: String,
    val useSpeech: Boolean
): Context

data class Connection(
    val sid: String,
    val upgrades: List<String>,
    val pingInterval: Int,
    val pingTimeout: Int
): Context

data class Error(
    val reason: String
): Context


val mapper = jacksonObjectMapper()
val donatePattern = Pattern.compile("\\[\"(.*?)\", \"\\{(.*?)}\"]")!!
val jsonRegex = Pattern.compile("\\{.*}").toRegex()

fun parseToContext(message: String): Context {
    val donateMatcher = donatePattern.matcher(message)
    if (donateMatcher.find()) {
        val res = jsonRegex.find(message)
        return mapper.readValue<Donate>(res?.value!!)
    }

    val jsonPart = message.dropWhile { it.isDigit() || it == '-' }
    val json: JsonNode = mapper.readTree(jsonPart)

    return when {
        json.isArray -> {
            val type = json[0].asText()
            when (type) {
                "error" -> Error(json[1].asText())
                else -> throw IllegalArgumentException()
            }
        }
        json.isObject -> {
            if (json.has("sid")) {
                mapper.readValue<Connection>(jsonPart)
            }
            else {
                throw IllegalArgumentException()
            }
        }
        else -> throw IllegalArgumentException()
    }
}

