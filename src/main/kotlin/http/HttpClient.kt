package http

import http.backend.HttpBackend
import http.backend.HttpRequestData
import http.request.HttpRequestPipeline
import http.response.HttpResponsePipeline

class HttpClient(
        val backend: HttpBackend,
        block: HttpClient.() -> Unit = {}
) : HttpClientScope {
    override val parent: HttpClientScope = EmptyScope
    override val requestPipeline = HttpRequestPipeline()
    override val responsePipeline = HttpResponsePipeline()

    init {
        requestPipeline.intercept(HttpRequestPipeline.Send, { requestData ->
            val data = when (requestData) {
                is HttpRequestData -> requestData
                is HttpRequestData.Builder -> requestData.build()
                else -> error("Unknown format of request: $requestData")
            }

            proceedWith(backend.makeRequest(data))
        })

        block()
    }
}

