package http.examples

import http.*
import http.backend.jvm.ApacheBackend
import http.request.request
import kotlinx.coroutines.experimental.runBlocking

suspend fun requests() {
    val client = HttpClient(ApacheBackend)

    val request = request {
        url(host = "google.com")
        url {
            parameters["q"] = "Hello, world"
        }
    }

    val response = client.execute<String>(request)
    println(response)
    client.close()
}

fun main(args: Array<String>) {
    runBlocking {
        requests()
    }
}
