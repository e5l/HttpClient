package http

import execute
import http.call.HttpClientCall
import http.pipeline.ClientScope
import http.pipeline.buildRequestPipeline
import http.pipeline.buildResponsePipeline
import http.request.RequestBuilder
import http.response.ResponseData
import org.jetbrains.ktor.util.URLProtocol

fun ClientScope.request(block: RequestBuilder.() -> Unit): HttpClientCall =
        HttpClientCall(buildRequestPipeline(), buildResponsePipeline(), RequestBuilder().apply(block))

suspend inline fun <reified T> ClientScope.execute(builder: RequestBuilder, requestData: Any = Unit): T {
    return HttpClientCall(buildRequestPipeline(), buildResponsePipeline(), builder).execute(requestData)
}

suspend fun ClientScope.executeCall(requestData: Any, block: RequestBuilder.() -> Unit): ResponseData =
        execute(RequestBuilder().apply(block), requestData)

suspend fun ClientScope.get(
        host: String = "localhost",
        path: String = "",
        port: Int = 80,
        scheme: String = "http"
): ResponseData = executeCall(Unit) {
    url(host, port, path)
    this.scheme = scheme
}

suspend fun ClientScope.call(block: RequestBuilder.() -> Unit): HttpClientCall = TODO()
