package core.webclient

import io.ktor.client.*
import io.ktor.client.engine.mock.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.http.*
import io.ktor.serialization.jackson.*
import io.pazzk.core.webclient.RestClient
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import kotlin.test.Test

internal class RestClientTest {

    @Test
    fun testGet() = runBlocking {
        val mockEngine = MockEngine { _ ->
            respond(
                content = """{"code": 200, "message": null, "content": 
                    |{"sessionUrl": "https://abc?auth=abc"}}""".trimMargin(),
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString())
            )
        }

        val client = HttpClient(engine = mockEngine) {
            install(ContentNegotiation) {
                jackson()
            }
        }

        val webClient = RestClient("abc", client)
        val response = webClient.get()

        assertThat(response).isNotNull
        assertThat(response?.code).isEqualTo(200)
        assertThat(response?.content?.sessionUrl).isEqualTo("https://abc?auth=abc")

        webClient.close()
    }

}