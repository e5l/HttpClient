package http.tests

import http.HttpClient
import http.backend.jvm.ApacheBackend
import http.execute
import http.features.PlainText
import http.features.install
import http.request.body
import http.request.request
import http.tests.utils.TestWithKtor
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.ktor.content.readText
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.http.withCharset
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.post
import org.jetbrains.ktor.routing.routing
import org.junit.Test
import java.nio.charset.Charset
import java.util.*


class PostTests : TestWithKtor() {
    override val server = embeddedServer(Netty, 8080) {
        routing {
            post("/") {
                val content = call.request.receiveContent().readText()
                assert(content.startsWith("Hello, post"))
                call.respondText(content)
            }
        }
    }

    @Test
    fun postString() {
        postHelper("Hello, post")
    }

    @Test
    fun hugePost() {
        val builder = StringBuilder()
        val STRING_SIZE = 1024 * 1024 * 32
        val random = Random()

        while (builder.length < STRING_SIZE) {
            builder.append(random.nextInt(256).toChar())
        }

        postHelper("Hello, post: $builder")
    }

    private fun postHelper(sendText: String) {
        val client = HttpClient(ApacheBackend) {
            install(PlainText)
        }

        val request = request {
            url {
                port = 8080
            }
            method = HttpMethod.Post
            body(sendText)
            headers { set(HttpHeaders.ContentType, ContentType.Text.Plain.withCharset(Charset.defaultCharset()).toString()) }
        }

        val response = runBlocking { client.execute<String>(request) }
        assert(response == sendText)

        client.close()
    }

}
