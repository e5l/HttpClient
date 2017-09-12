package http

import execute
import http.call.HttpClientCall
import http.pipeline.ClientScope
import http.pipeline.buildRequestPipeline
import http.pipeline.buildResponsePipeline
import http.request.RequestDataBuilder
import org.jetbrains.ktor.util.URLProtocol

fun ClientScope.request(block: RequestDataBuilder.() -> Unit): HttpClientCall =
        HttpClientCall(buildRequestPipeline(), buildResponsePipeline(), RequestDataBuilder().apply(block))

fun ClientScope.call(block: RequestDataBuilder.() -> Unit): HttpClientCall = request(block)

suspend inline fun <reified T> ClientScope.execute(builder: RequestDataBuilder, requestData: Any = Unit): T =
        HttpClientCall(buildRequestPipeline(), buildResponsePipeline(), builder).execute(requestData)

suspend inline fun <reified T> ClientScope.executeCall(requestData: Any, block: RequestDataBuilder.() -> Unit): T =
        execute(RequestDataBuilder().apply(block), requestData)

suspend inline fun <reified T> ClientScope.get(
        host: String = "localhost",
        path: String = "",
        port: Int = 80,
        scheme: String = "http"
): T = executeCall(Unit) {
    url(host, URLProtocol(scheme, port), path)
    url.port = port
}
