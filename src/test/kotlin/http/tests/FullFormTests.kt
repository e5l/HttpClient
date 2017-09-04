package http.tests

import http.*
import http.backend.jvm.ApacheBackend
import http.features.PlainText
import http.features.install
import http.request.request
import http.tests.utils.TestWithKtor
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.request.receive
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.post
import org.jetbrains.ktor.routing.routing
import org.jetbrains.ktor.util.URLProtocol
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
                    host = "localhost"
                    protocol = URLProtocol.HTTP
                    port = 8080
                    path("hello")
                    method = HttpMethod.Get
                }
            }.bodyText()

            assert(text == "Hello, world")
        }

        client.close()
    }

    @Test
    fun testRequest() {
        val client = HttpClient(ApacheBackend) {
            install(PlainText)
        }

        val request = request {
            url {
                host = "localhost"
                protocol = URLProtocol.HTTP
                port = 8080
                path("hello")
                method = HttpMethod.Get
            }
        }

        val body = runBlocking { client.execute<String>(request) }
        assert(body == "Hello, world")
    }
}