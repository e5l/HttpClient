package http.examples

import http.HttpClient
import http.pipeline.HttpClientScope
import http.backend.jvm.ApacheBackend

private fun HttpClientScope.get(s: String): HttpClientScope = TODO()
private fun <T> HttpClientScope.responseBody(): Any = TODO()

class GithubCli {
}

fun main(args: Array<String>) {
    val client = HttpClient(ApacheBackend)

    val github = client.get("https://api.github.com")
    val response = github.get("repos/jetbrains/kotlin").responseBody<String>()

    println(response)
}
