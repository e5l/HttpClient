package http.examples

import http.*
import http.backend.jvm.ApacheBackend
import http.pipeline.CallScope
import http.pipeline.ClientScope
import http.request.request
import kotlinx.coroutines.experimental.runBlocking

abstract class HttpConnection(parent: ClientScope) : CallScope(parent)

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

