package http.examples

import http.HttpClient
import http.backend.jvm.ApacheBackend
import http.execute
import http.pipeline.CallScope
import http.pipeline.HttpClientScope
import http.request.request
import http.url
import kotlinx.coroutines.experimental.runBlocking

abstract class HttpConnection(parent: HttpClientScope) : CallScope(parent)

suspend fun connect() {
    val client = HttpClient(ApacheBackend)
    val request = request {
        url(host = "api.github.com")
    }

    val connection = client.execute<HttpConnection>(request)
}

fun main(args: Array<String>) {
    runBlocking {
        connect()
    }
}

