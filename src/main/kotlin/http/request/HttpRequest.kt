package http.request

import http.call.HttpCall


interface HttpRequest {
    val call: HttpCall
    val pipeline: HttpRequestPipeline
}

open class BaseHttpRequest(override val call: HttpCall, override val pipeline: HttpRequestPipeline) : HttpRequest

suspend fun HttpCall.makeRequest() = request.pipeline.execute(this, requestData)
