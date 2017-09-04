package http.call

import http.pipeline.*
import http.request.*
import http.response.*
import org.jetbrains.ktor.util.URLProtocol

interface HttpCall {
    val requestData: Any
    val request: HttpRequest
    val response: HttpResponse
}

class BaseHttpCall(
        requestPipeline: HttpRequestPipeline,
        responsePipeline: HttpResponsePipeline,
        override val requestData: Any
) : HttpCall {
    override val request = BaseHttpRequest(this, requestPipeline)
    override val response = BaseHttpResponse(this, responsePipeline)
}

suspend fun HttpClientScope.call(block: HttpRequestDataBuilder.() -> Unit): HttpResponseData {
    val scope = CallScope(this)
    val request = HttpRequestDataBuilder().apply(block).build()

    val call = BaseHttpCall(
            scope.buildRequestPipeline(),
            scope.buildResponsePipeline(),
            request
    )

    return execute(call)
}

suspend fun HttpClientScope.get(
        host: String = "localhost",
        port: Int = 80,
        path: String = "",
        protocol: URLProtocol = URLProtocol.HTTP
): HttpResponseData =
        call {
            url(host, port, path)
            this.protocol = protocol
        }
