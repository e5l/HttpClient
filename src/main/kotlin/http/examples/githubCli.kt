package http.examples

import http.HttpClient
import http.backend.jvm.ApacheBackend

fun main(args: Array<String>) {
    val client = HttpClient(ApacheBackend())

//    val github = client.get("https://api.github.com")
//    val response = github.get("repos/jetbrains/kotlin").responseBody<String>()
//    println(response)
}

