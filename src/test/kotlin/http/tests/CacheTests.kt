package http.tests

import http.HttpClient
import http.backend.jvm.ApacheBackend
import http.features.HttpCache
import http.get
import http.pipeline.config
import http.request
import http.request.HttpRequestBuilder
import http.tests.utils.TestWithKtor
import http.utils.*
import io.ktor.content.CacheControl
import io.ktor.features.withETag
import io.ktor.features.withLastModified
import io.ktor.host.ApplicationHost
import io.ktor.host.embeddedServer
import io.ktor.netty.Netty
import io.ktor.pipeline.*
import io.ktor.response.cacheControl
import io.ktor.response.respondText
import io.ktor.routing.routing
import io.ktor.routing.get
import kotlinx.coroutines.experimental.runBlocking
import org.apache.http.client.methods.*
import org.junit.Test
import java.util.*
import java.util.concurrent.atomic.AtomicInteger

class CacheTests : TestWithKtor() {
    var counter = AtomicInteger()
    override val server: ApplicationHost = embeddedServer(Netty, 8080) {
        routing {
            get("/reset") {
                counter.set(0)
                call.respondText("")
            }
            get("/nocache") {
                counter.incrementAndGet()
                call.response.cacheControl(CacheControl.NoCache(null))
                call.respondText(counter.toString())
            }
            get("/nostore") {
                counter.incrementAndGet()
                call.response.cacheControl(CacheControl.NoStore(null))
                call.respondText(counter.toString())
            }
            get("/maxAge") {
                counter.incrementAndGet()
                call.response.cacheControl(CacheControl.MaxAge(5))
                call.respondText(counter.get().toString())
            }
            get("/etag") {
                val etag = if (counter.get() < 2) "0" else "1"
                counter.incrementAndGet()
                call.withETag(etag.toString()) {
                    call.respondText(counter.get().toString())
                }
            }
        }
    }

    @Test
    fun testDisabled() {
        val client = HttpClient(ApacheBackend).config {
            install(HttpCache)
        }

        val builder = HttpRequestBuilder().apply {
            url(port = 8080)
        }

        runBlocking {
            listOf("nocache", "nostore").forEach {
                builder.url.path = it
                assert(client.get<String>(builder) != client.get<String>(builder))
            }
        }
    }

    @Test
    fun maxAge() {
        val client = HttpClient(ApacheBackend).config {
            install(HttpCache)
        }

        val results = mutableListOf<String>()
        val request = HttpRequestBuilder().apply {
            url(path = "maxAge", port = 8080)
        }

        runBlocking {
            results += client.get<String>(request)
            results += client.get<String>(request)

            Thread.sleep(7 * 1000)

            results += client.get<String>(request)
            results += client.get<String>(request)
        }

        assert(results[0] == results[1])
        assert(results[2] == results[3])
        assert(results[0] != results[2])
    }

}