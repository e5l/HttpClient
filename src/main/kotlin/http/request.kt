package http

import execute
import http.call.BaseHttpCall
import http.pipeline.CallScope
import http.pipeline.HttpClientScope
import http.pipeline.buildRequestPipeline
import http.pipeline.buildResponsePipeline
import http.request.HttpRequestDataBuilder
import http.response.HttpResponseData
import org.jetbrains.ktor.util.URLProtocol

suspend inline fun <reified T> HttpClientScope.execute(request: HttpRequestDataBuilder): T {
    val scope = CallScope(this)
    val call = BaseHttpCall(scope.buildRequestPipeline(), scope.buildResponsePipeline(), request)
    return execute(call)
}

suspend fun HttpClientScope.call(block: HttpRequestDataBuilder.() -> Unit): HttpResponseData =
        execute(HttpRequestDataBuilder().apply(block))

suspend fun HttpClientScope.get(
        host: String = "localhost",
        port: Int = 80,
        path: String = "",
        protocol: URLProtocol = URLProtocol.HTTP
): HttpResponseData = call {
    url(host, port, path)
    this.protocol = protocol
}
