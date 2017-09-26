package http.tests

import http.HttpClient
import http.backend.jvm.ApacheBackend
import http.post
import http.tests.utils.TestWithKtor
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.ktor.content.readText
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.response.contentType
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

    private fun postHelper(test: String) {
        val client = HttpClient(ApacheBackend)

        val response = runBlocking {
            client.post<String>(port = 8080, payload = test) {
                headers.contentType(ContentType.Text.Plain.withCharset(Charset.defaultCharset()))
            }
        }
        assert(response == test)

        client.close()
    }

}
