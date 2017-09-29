package http.examples

import http.HttpClient
import http.backend.jvm.ApacheBackend
import http.call.call
import http.pipeline.ClientScope
import http.receiveText
import http.utils.userAgent
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.http.response.contentType

suspend fun full(client: ClientScope) {
    val searchResults = client.call {
        url {
            scheme = "https"
            host = "google.com"
            port = 443
            path = "search"
            method = HttpMethod.Get
            queryParameters.append("q", "hello, world")
        }
    }

    println("google: ${searchResults.receiveText()}")

    val redditFrontJson = client.call {
        url {
            scheme = "https"
            host = "reddit.com"
            path = ".json"
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

