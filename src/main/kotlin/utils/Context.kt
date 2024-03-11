package io.pazzk.utils

import com.fasterxml.jackson.annotation.JsonIgnoreProperties
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.util.regex.Pattern

sealed interface Context

data class Donate(
    val donationId: String,
    val animationUrl: String,
    val profile: Profile?,
    val payAmount: Int,
    val donationText: String,
    val type: String,
    val isAnonymous: Boolean,
    val alertSoundUrl: String,
    val useSpeech: Boolean
): Context

// I don't know about profile's badge and title
// maybe there more property in json body?
data class Profile(
    val userIdHash: String,
    val nickname: String,
    val profileImageUrl: String?,
    val userRoleCode: String?,
    val badge: String?,
    val title: String?,
    val verifiedMark: Boolean,
    val activityBadges: List<ActivityBadge>?,
    val streamingProperty: StreamingProperty?
)

@JsonIgnoreProperties(ignoreUnknown = true)
data class ActivityBadge(
    val badgeNo: Int?,
    val badgeId: String?,
    val imageUrl: String?,
    val activate: Boolean?
)

/* Probably It is rank of donation (gold, sliver, etc..) */
data class StreamingProperty(
    val realTimeDonationRanking: DonationRanking?
)

data class DonationRanking(
    val badge: Badge?
)

data class Badge(
    val imageUrl: String?
)

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
val donatePattern = Pattern.compile("\\[\"(.*?)\",\"\\{(.*?)}\"]")!!
val jsonRegex = Pattern.compile("\\{.*}").toRegex()
val logger: Logger = LoggerFactory.getLogger("Context")

fun parseToContext(message: String): Context {
    if (logger.isDebugEnabled) {
        logger.debug("message receive: {}", message)
    }
    val donateMatcher = donatePattern.matcher(message)
    if (donateMatcher.find()) {
        val res = jsonRegex.find(message)
        val removeBackSlash = (res?.value!!).replace("\\\"", "\"")
            .replace("\\", "")
            .replace("\"{", "{")
            .replace("}\"", "}")
        return mapper.readValue<Donate>(removeBackSlash)
    }

    val jsonPart = message.dropWhile { it.isDigit() || it == '-' }
    val json: JsonNode = mapper.readTree(jsonPart)

    return when {
        json.isArray -> {
            when (val type = json[0].asText()) {
                "error" -> Error(json[1].asText())
                else -> throw IllegalArgumentException(type)
            }
        }
        json.isObject -> {
            if (json.has("sid")) {
                mapper.readValue<Connection>(jsonPart)
            }
            else {
                throw IllegalArgumentException(jsonPart)
            }
        }
        else -> throw IllegalArgumentException(jsonPart)
    }
}

fun String.isInt(): Boolean = this.toIntOrNull() != null
