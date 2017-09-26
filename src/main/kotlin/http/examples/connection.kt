package http.examples

import http.HttpClient
import http.backend.jvm.ApacheBackend
import http.pipeline.CallScope
import http.request
import http.url
import kotlinx.coroutines.experimental.runBlocking

abstract class HttpConnection : CallScope()

suspend fun connect() {
    HttpClient(ApacheBackend).use { client ->

        val requestBuilder = request {
            url(host = "api.github.com")
        }

        val connection = client.request<HttpConnection>(requestBuilder)
    }
}

fun main(args: Array<String>) {
    runBlocking {
        connect()
    }
}

