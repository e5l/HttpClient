package http.examples

import http.HttpClient
import http.backend.jvm.ApacheBackend
import http.request
import http.url
import kotlinx.coroutines.experimental.runBlocking

suspend fun requests() {
    val client = HttpClient(ApacheBackend)

    val requestBuilder = request {
        url(host = "google.com")
        url {
            queryParameters["q"] = "Hello, world"
        }
    }

    val response = client.request<String>(requestBuilder)
    println(response)

    client.close()
}

fun main(args: Array<String>) {
    runBlocking {
        requests()
    }
}
