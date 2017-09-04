package http.examples

import http.*
import http.backend.jvm.ApacheBackend
import http.response.HttpResponseData
import kotlinx.coroutines.experimental.runBlocking

suspend fun requests() {
    val client = HttpClient(ApacheBackend)

    val request = request {
        url(host = "google.com") {
            parameters["q"] = "Hello, world"
        }
    }

    val response = client.execute<HttpResponseData>(request)
    println(response.debug())
    client.close()
}

fun main(args: Array<String>) {
    runBlocking {
        requests()
    }
}
