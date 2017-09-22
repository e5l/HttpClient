package http.examples

import http.HttpClient
import http.backend.jvm.ApacheBackend
import http.call.call
import http.receiveText
import http.request.userAgent
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.http.util.URLProtocol
import org.jetbrains.ktor.response.contentType

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

    println("google: ${searchResults.receiveText()}")

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
            userAgent("Kotlin HttpClient")
            append(HttpHeaders.SetCookie, "name=vasya")
        }
    }

//    redditFrontJson.isSuccess()
//    redditFrontJson.statusCode
//    redditFrontJson.request.scheme
//    redditFrontJson.request.method
//    redditFrontJson.remote.ip
//    redditFrontJson.remote.port
//    redditFrontJson.request.uri
//    redditFrontJson.request.parameters
//    redditFrontJson.response.charset
//    redditFrontJson.request.formParameters<LoginForm>()

    println("reddit: ${redditFrontJson.receiveText()}")
}

fun main(args: Array<String>) {
    runBlocking {
        HttpClient(ApacheBackend).use {
            full(it)
        }
    }
}

