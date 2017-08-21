package http

import http.backend.jvm.ApacheBackend
import http.features.PathRequest
import http.features.PlainText
import http.features.install
import http.response.asExpected

fun main(args: Array<String>) {
    val httpClient = HttpClient(ApacheBackend()) {
        install(PathRequest)
    }

    httpClient.session {
        val google = connect("google.com") {
            install(PlainText)
        }

        val response = google.get("")
        println(response.asExpected<String>())
    }
}

