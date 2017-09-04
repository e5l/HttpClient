package http.response

import http.call.HttpCall
import http.request.HttpRequestData
import http.request.makeRequest

interface HttpResponse {
    val call: HttpCall
    val pipeline: HttpResponsePipeline
}

class BaseHttpResponse(
        override val call: HttpCall,
        override val pipeline: HttpResponsePipeline
) : HttpResponse

inline suspend fun <reified T> HttpCall.makeResponse(
        requestData: HttpRequestData,
        responseData: HttpResponseData
): ResponseContainer = response.pipeline.execute(this, ResponseContainer(T::class, requestData, responseData))
