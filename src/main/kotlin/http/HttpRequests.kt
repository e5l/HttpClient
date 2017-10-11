package http

import http.call.call
import http.call.receive
import http.pipeline.HttpClientScope
import http.request.HttpRequestBuilder
import http.utils.takeFrom
import http.utils.url
import io.ktor.http.HttpMethod
import java.net.URL

suspend inline fun <reified T> HttpClientScope.request(builder: HttpRequestBuilder = HttpRequestBuilder()): T =
        call(builder).receive<T>()

suspend inline fun <reified T> HttpClientScope.request(block: HttpRequestBuilder.() -> Unit): T =
        request(HttpRequestBuilder().apply(block))

suspend inline fun <reified T> HttpClientScope.get(
        scheme: String = "http", host: String = "localhost", port: Int = 80,
        path: String = "",
        payload: Any = Unit,
        block: HttpRequestBuilder.() -> Unit
): T = request {
    url(scheme, host, port, path)
    method = HttpMethod.Get
    this.payload = payload
    apply(block)
}

suspend inline fun <reified T> HttpClientScope.get(
        scheme: String = "http", host: String = "localhost", port: Int = 80,
        path: String = "",
        payload: Any = Unit
): T = get(scheme, host, port, path, payload, {})

suspend inline fun <reified T> HttpClientScope.get(data: URL): T = get {
    url.takeFrom(data)
}

suspend inline fun <reified T> HttpClientScope.get(url: String): T = get(URL(url))

suspend inline fun <reified T> HttpClientScope.post(
        scheme: String = "http", host: String = "localhost", port: Int = 80,
        path: String = "",
        payload: Any = Unit,
        block: HttpRequestBuilder.() -> Unit
): T = request {
    url(scheme, host, port, path)
    method = HttpMethod.Post
    this.payload = payload
    apply(block)
}

suspend inline fun <reified T> HttpClientScope.post(
        scheme: String = "http", host: String = "localhost", port: Int = 80,
        path: String = "",
        payload: Any = Unit
): T = post(scheme, host, port, path, payload, {})

fun request(block: HttpRequestBuilder.() -> Unit) = HttpRequestBuilder().apply(block)