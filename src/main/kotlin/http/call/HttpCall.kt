package http.call

import http.request.*
import http.response.*
import org.jetbrains.ktor.util.Attributes

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

data class HttpCallData(val request: HttpRequestData, val response: HttpResponseData)
