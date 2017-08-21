package http

import http.backend.HttpRequestData
import http.request.HttpRequestPipeline
import http.features.PathRequest
import http.response.HttpResponse
import http.response.HttpResponsePipeline

class HttpConnection(val session: HttpClientSession, resource: String) : HttpClientScope {
    override val parent: HttpClientScope = session
    override val requestPipeline = HttpRequestPipeline()
    override val responsePipeline = HttpResponsePipeline()

    init {
        requestPipeline.intercept(HttpRequestPipeline.Address) { request ->
            if (request is HttpRequestData.Builder) {
                request.url = resource
            }
        }
    }

    suspend fun request(requestData: Any): HttpResponse {
        val request = buildRequestPipeline()
        val response = buildResponsePipeline()
        val call = BaseHttpCall(request, response, requestData)

        return call.response
    }

    private fun buildResponsePipeline(): HttpResponsePipeline = HttpResponsePipeline().apply {
        visit(after = { merge(it.responsePipeline) })
    }

    private fun buildRequestPipeline(): HttpRequestPipeline = HttpRequestPipeline().apply {
        visit(after = { merge(it.requestPipeline) })
    }
}

suspend fun HttpConnection.get(path: String): HttpResponse = request(PathRequest(path))