package http.tests

import http.HttpClient
import http.backend.jvm.ApacheBackend
import http.features.json.GsonSerializer
import http.features.json.Json
import http.get
import http.pipeline.HttpClientScope
import http.pipeline.config
import http.tests.utils.TestWithKtor
import io.ktor.host.ApplicationHost
import io.ktor.host.embeddedServer
import io.ktor.http.ContentType
import io.ktor.netty.Netty
import io.ktor.routing.*
import io.ktor.pipeline.*
import io.ktor.request.*
import io.ktor.response.*
import org.junit.Assert.assertEquals
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JSON
import org.junit.Test

class JsonTests : TestWithKtor() {
    override val server: ApplicationHost = embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                val text = call.receiveText()
                val request = JSON.parse<Request>(text)
                call.respondText(
                        Response(request.id, request.name).serialize(),
                        ContentType.Application.Json
                )
            }
        }
    }

    @Serializable
    data class Request(val id: Int, val name: String? = null)

    @Serializable
    data class Response(val requestId: Int, val name: String? = null)

    @Test
    fun simpleJson() {
        val client = HttpClient(ApacheBackend).config {
            install(Json)
        }

        simpleTest(client)
    }

    @Test
    fun simpleGson() {
        val client = HttpClient(ApacheBackend).config {
            install(Json) {
                serializer = GsonSerializer()
            }
        }

        simpleTest(client)
    }


    private fun simpleTest(client: HttpClientScope) {

        val request = Request(1)
        val response = runBlocking {
            client.get<Response>(port = 8080, payload = request.serialize())
        }

        assertEquals(request.id, response.requestId)
        assertEquals(request.name, response.name)
    }

    private inline fun <reified T : Any> T.serialize(): String = JSON.stringify<T>(this)
}
