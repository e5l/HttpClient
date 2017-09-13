package http.tests

import http.HttpClient
import http.backend.jvm.ApacheBackend
import http.execute
import http.request.request
import http.tests.utils.TestWithKtor
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.ktor.application.ApplicationCall
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
    val BODY_PREFIX = "Hello, post"

    override val server = embeddedServer(Netty, 8080) {
        routing {
            post("/") {
                val content = call.request.receiveContent().readText()
                assert(content.startsWith(BODY_PREFIX))
                call.respondText(content)
            }
        }
    }

    @Test
    fun postString() {
        postHelper(BODY_PREFIX)
    }

    @Test
    fun hugePost() {
        val builder = StringBuilder()
        val STRING_SIZE = 1024 * 1024 * 32
        val random = Random()

        while (builder.length < STRING_SIZE) {
            builder.append(random.nextInt(256).toChar())
        }

        postHelper("$BODY_PREFIX: $builder")
    }

    private fun postHelper(sendText: String) {
        val client = HttpClient(ApacheBackend)

        val request = request {
            url {
                port = 8080
            }
            method = HttpMethod.Post
            headers { set(HttpHeaders.ContentType, ContentType.Text.Plain.withCharset(Charset.defaultCharset()).toString()) }
        }

        val response = runBlocking {
            client.execute<String>(request, sendText)
        }
        assert(response == sendText)

        client.close()
    }

}
