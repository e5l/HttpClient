package http.examples

import http.HttpClient
import http.backend.jvm.ApacheBackend
import http.features.Cache
import http.get
import http.pipeline.config
import kotlinx.coroutines.experimental.runBlocking

suspend fun requests() {
    val client = HttpClient(ApacheBackend).config {
        install(Cache)
    }

    println(client.get<String>("https://en.wikipedia.org/wiki/Hello, World!"))
    println(client.get<String>("https://en.wikipedia.org/wiki/Hello, World!"))
}

fun main(args: Array<String>) {
    runBlocking {
        requests()
    }
}
