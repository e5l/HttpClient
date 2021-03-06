package http.tests

import http.HttpClient
import http.backend.jvm.ApacheBackend
import http.post
import http.tests.utils.TestWithKtor
import io.ktor.content.readText
import io.ktor.host.embeddedServer
import io.ktor.http.ContentType
import io.ktor.http.withCharset
import io.ktor.http.response.contentType
import io.ktor.netty.Netty
import io.ktor.routing.routing
import io.ktor.routing.post
import io.ktor.pipeline.call
import io.ktor.response.respondText
import kotlinx.coroutines.experimental.runBlocking
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

    private fun postHelper(text: String) {
        val client = HttpClient(ApacheBackend)

        val response = runBlocking {
            client.post<String>(port = 8080, payload = text) {
                headers.contentType(ContentType.Text.Plain.withCharset(Charset.defaultCharset()))
            }
        }
        assert(response == text)

        client.close()
    }

}
