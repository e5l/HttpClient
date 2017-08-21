package http

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
