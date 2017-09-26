package http

import http.call.call
import http.call.receive
import http.pipeline.ClientScope
import http.request.RequestBuilder
import org.jetbrains.ktor.http.HttpMethod

suspend inline fun <reified T> ClientScope.request(builder: RequestBuilder = RequestBuilder()): T =
        call(builder).receive<T>()

suspend inline fun <reified T> ClientScope.request(block: RequestBuilder.() -> Unit): T =
        request(RequestBuilder().apply(block))

suspend inline fun <reified T> ClientScope.get(
        scheme: String = "http", host: String = "localhost", port: Int = 80,
        path: String = "",
        payload: Any = Unit,
        block: RequestBuilder.() -> Unit
): T = request {
    url(scheme, host, port, path)
    method = HttpMethod.Get
    this.payload = payload
    apply(block)
}

suspend inline fun <reified T> ClientScope.get(
        scheme: String = "http", host: String = "localhost", port: Int = 80,
        path: String = "",
        payload: Any = Unit
): T = get(scheme, host, port, path, payload, {})

suspend inline fun <reified T> ClientScope.post(
        scheme: String = "http", host: String = "localhost", port: Int = 80,
        path: String = "",
        payload: Any = Unit,
        block: RequestBuilder.() -> Unit
): T = request {
    url(scheme, host, port, path)
    method = HttpMethod.Post
    this.payload = payload
    apply(block)
}

suspend inline fun <reified T> ClientScope.post(
        scheme: String = "http", host: String = "localhost", port: Int = 80,
        path: String = "",
        payload: Any = Unit
): T = post(scheme, host, port, path, payload, {})

fun request(block: RequestBuilder.() -> Unit) = RequestBuilder().apply(block)