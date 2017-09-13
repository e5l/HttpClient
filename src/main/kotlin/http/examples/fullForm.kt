package http.examples

import http.*
import http.backend.jvm.ApacheBackend
import http.call.call
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.response.contentType
import org.jetbrains.ktor.util.URLProtocol

suspend fun full(client: HttpClient) {
    val searchResults = client.call {
        url {
            host = "google.com"
            protocol = URLProtocol.HTTPS
            port = 443
            path("search")
            method = HttpMethod.Get

            parameters.append("q", "hello, world")
        }
    }

    println("google: ${searchResults.bodyText()}")

    val redditFrontJson = client.call {
        url {
            host = "reddit.com"
            path(".json")
            protocol = URLProtocol.HTTPS
            port = 443
            method = HttpMethod.Get
        }

        headers {
            contentType(ContentType.Application.Json)
            append(HttpHeaders.UserAgent, "Kotlin HttpClient")
            append(HttpHeaders.SetCookie, "name=vasya")
        }
    }

    println("reddit: ${redditFrontJson.bodyText()}")
}

fun main(args: Array<String>) {
    runBlocking {
        HttpClient(ApacheBackend).use {
            full(it)
        }
    }
}

