package http.response

import http.call.HttpCall
import http.request.makeRequest

interface HttpResponse {
    val call: HttpCall
    val pipeline: HttpResponsePipeline
}

class BaseHttpResponse(override val call: HttpCall, override val pipeline: HttpResponsePipeline) : HttpResponse {
}

inline suspend fun <reified T> HttpCall.makeResponse(rawResponse: Any): ResponseContainer
        = response.pipeline.execute(this, ResponseContainer(T::class, rawResponse))

inline suspend fun <reified T> HttpResponse.asExpected(): T {
    val response = call.makeResponse<T>(call.makeRequest())
    return response.value as? T ?: error("Invalid type: $response")
}