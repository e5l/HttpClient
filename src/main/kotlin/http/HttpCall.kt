package http

import http.backend.HttpRequestDataBuilder
import http.backend.HttpResponseData
import http.backend.execute
import http.request.BaseHttpRequest
import http.request.HttpRequest
import http.request.HttpRequestPipeline
import http.response.BaseHttpResponse
import http.response.HttpResponse
import http.response.HttpResponsePipeline

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

class CallScope(override val parent: HttpClientScope) : HttpClientScope {
    override val requestPipeline = HttpRequestPipeline()
    override val responsePipeline = HttpResponsePipeline()
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