package http

import http.backend.HttpRequestDataBuilder
import http.request.HttpRequestPipeline
import http.response.HttpResponse
import http.response.HttpResponsePipeline

class HttpConnection(override val parent: HttpClientScope, resource: String) : HttpClientScope {
    override val requestPipeline = HttpRequestPipeline()
    override val responsePipeline = HttpResponsePipeline()

    init {
        requestPipeline.intercept(HttpRequestPipeline.Address) { request ->
            if (request is HttpRequestDataBuilder) {
                request.url {
                    host = resource
                }
            }
        }
    }

    suspend fun request(requestData: Any): HttpResponse {
        val request = buildRequestPipeline()
        val response = buildResponsePipeline()
        val call = BaseHttpCall(request, response, requestData)

        return call.response
    }
}
