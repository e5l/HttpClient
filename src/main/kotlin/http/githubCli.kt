package http

import http.backend.jvm.ApacheBackend
import http.features.Https
import http.features.PathRequest
import http.features.PlainText
import http.features.install
import http.response.asExpected

fun main(args: Array<String>) {
    val httpClient = HttpClient(ApacheBackend()) {
        install(PathRequest)
    }

    httpClient.session {
        val github = connect("api.github.com") {
            install(PlainText)
            install(Https)
        }

        val response = github.get("repos/jetbrains/kotlin")
        println(response.asExpected<String>())
    }
}

