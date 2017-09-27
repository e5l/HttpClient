package http.tests

import http.HttpClient
import http.backend.jvm.ApacheBackend
import http.call.call
import http.receiveText
import http.request
import http.tests.utils.TestWithKtor
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.pipeline.call
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.post
import org.jetbrains.ktor.routing.routing
import org.junit.Test

class FullFormTests : TestWithKtor() {
    override val server = embeddedServer(Netty, 8080) {
        routing {
            get("/hello") {
                call.respondText("Hello, world")
            }
            post("/hello") {
                assert(call.receive<String>() == "Hello, world")
                call.respondText("")
            }
        }
    }

    @Test
    fun testGet() {
        val client = HttpClient(ApacheBackend)
        runBlocking {
            val text = client.call {
                url {
                    scheme = "http"
                    host = "localhost"
                    port = 8080
                    path = "hello"
                    method = HttpMethod.Get
                }
            }.receiveText()

            assert(text == "Hello, world")
        }

        client.close()
    }

    @Test
    fun testRequest() {
        val client = HttpClient(ApacheBackend)

        val requestBuilder = request {
            url {
                host = "localhost"
                scheme = "http"
                port = 8080
                path = "hello"
                method = HttpMethod.Get
            }
        }

        val body = runBlocking { client.request<String>(requestBuilder) }
        assert(body == "Hello, world")
    }
}