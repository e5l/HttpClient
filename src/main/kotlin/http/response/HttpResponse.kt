package http.response

import http.HttpCall
import http.request.makeRequest

interface HttpResponse {
    val call: HttpCall
    val pipeline: HttpResponsePipeline
}

class BaseHttpResponse(override val call: HttpCall, override val pipeline: HttpResponsePipeline) : HttpResponse

suspend fun HttpCall.makeResponse(container: ResponseContainer) = response.pipeline.execute(this, container)

inline suspend fun <reified T> HttpResponse.asExpected(): T {
    val response = call.makeResponse(ResponseContainer(T::class, call.makeRequest()))
    return response.value as? T ?: error("Invalid type: $response")
}