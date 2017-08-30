package http.examples

import http.HttpClient
import http.backend.HttpResponseData
import http.backend.jvm.ApacheBackend
import http.call
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.util.URLProtocol
import java.io.InputStreamReader
import java.nio.charset.Charset

// TBD: write feature to parse headers
val HttpResponseData.charset: Charset
    get() = headers
            .getAll("Content-Type")
            ?.flatMap { it.split(";") }
            ?.find { it.contains("charset") }
            ?.split("=")
            ?.let { Charset.forName(it[1]) }
            ?: Charset.defaultCharset()

val HttpResponseData.bodyRawText: String
    get() = InputStreamReader(body, charset).readText()

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

    println("${searchResults.statusCode} ${searchResults.bodyRawText}")

    val redditFrontJson = client.call {
        url {
            host = "reddit.com"
            path(".json")
            protocol = URLProtocol.HTTPS
            port = 443
            method = HttpMethod.Get
        }

        headers {
            append(HttpHeaders.Accept, ContentType.Application.Json.toString())
            append(HttpHeaders.UserAgent, "Kotlin HttpClient")
            append(HttpHeaders.SetCookie, "name=vasya")
        }
    }

    println(redditFrontJson.bodyRawText)
}

fun main(args: Array<String>) {
    runBlocking {
        HttpClient(ApacheBackend()).use {
            full(it)
        }
    }
}

