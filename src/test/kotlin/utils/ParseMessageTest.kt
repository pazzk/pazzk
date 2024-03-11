package utils

import io.pazzk.utils.Connection
import io.pazzk.utils.Donate
import io.pazzk.utils.Error
import io.pazzk.utils.parseToContext
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

internal class ParseMessageTest {

    @Test
    fun testErrorMessage() {
        val errorMessage = "42[\"error\", \"fail cause\"]"

        val message = parseToContext(errorMessage)

        assertThat(message).isInstanceOf(Error::class.java)
    }

    @Test
    fun testConnectionMessage() {
        val connectionMessage =
            "0{\"sid\":\"00000000-1111-2222-3333-444444444444\",\"upgrades\":[\"abc\"],\"pingInterval\":-1,\"pingTimeout\":-5}"

        val message = parseToContext(connectionMessage)

        assertThat(message).isInstanceOf(Connection::class.java)
    }

    @Test
    fun testAnonymousDonationMessage() {
        val donationMessage =
            "42[\"donation\",\"{\\\"donationId\\\":\\\"1234567890qwertyuiopasdfghjkl\\\",\\\"animationUrl\\\":\\\"chat_donation_01.gif\\\",\\\"payAmount\\\":1000,\\\"donationText\\\":\\\"\\\",\\\"type\\\":\\\"nmeow\\\",\\\"isAnonymous\\\":true,\\\"alertSoundUrl\\\":\\\"https://ssl.pstatic.net/static/nng/glive/audio/new_cheeze.mp3\\\",\\\"useSpeech\\\":true}\"]"

        val message = parseToContext(donationMessage)

        assertThat(message).isInstanceOf(Donate::class.java)
        println(message)
    }

    @Test
    fun testUserDonationMessage() {
        val donationMessage =
            "42[\"donation\",\"{\\\"donationId\\\":\\\"lkjhgfdsapoiuytrewq0987654321\\\",\\\"animationUrl\\\":\\\"chat_donation_01.gif\\\",\\\"profile\\\":\\\"{\\\\\\\"userIdHash\\\\\\\":\\\\\\\"sampleHash\\\\\\\",\\\\\\\"nickname\\\\\\\":\\\\\\\"iqpizza6349\\\\\\\",\\\\\\\"profileImageUrl\\\\\\\":\\\\\\\"\\\\\\\",\\\\\\\"userRoleCode\\\\\\\":\\\\\\\"common_user\\\\\\\",\\\\\\\"badge\\\\\\\":null,\\\\\\\"title\\\\\\\":null,\\\\\\\"verifiedMark\\\\\\\":false,\\\\\\\"activityBadges\\\\\\\":[{\\\\\\\"badgeNo\\\\\\\":12345678,\\\\\\\"badgeId\\\\\\\":\\\\\\\"donation_newbie\\\\\\\",\\\\\\\"imageUrl\\\\\\\":\\\\\\\"fan.png\\\\\\\",\\\\\\\"activated\\\\\\\":true}],\\\\\\\"streamingProperty\\\\\\\":{\\\\\\\"realTimeDonationRanking\\\\\\\":{\\\\\\\"badge\\\\\\\":{\\\\\\\"imageUrl\\\\\\\":\\\\\\\"gold.png\\\\\\\"}}}}\\\",\\\"payAmount\\\":1000,\\\"donationText\\\":\\\"sample text\\\",\\\"type\\\":\\\"nmeow\\\",\\\"isAnonymous\\\":false,\\\"alertSoundUrl\\\":\\\"new_cheeze.mp3\\\",\\\"useSpeech\\\":true}\"]"

        val message = parseToContext(donationMessage)

        assertThat(message).isInstanceOf(Donate::class.java)
        println(message)
    }
}
