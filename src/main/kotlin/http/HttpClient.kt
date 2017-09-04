package http

import http.backend.HttpClientBackend
import http.backend.HttpClientBackendFactory
import http.call.HttpCallData
import http.pipeline.CallScope
import http.pipeline.EmptyScope
import http.request.HttpRequestDataBuilder
import http.request.HttpRequestPipeline
import http.response.HttpResponsePipeline

class HttpClient(
        backendFactory: HttpClientBackendFactory,
        block: HttpClient.() -> Unit = {}
) : CallScope(EmptyScope()) {
    override val requestPipeline = HttpRequestPipeline()
    override val responsePipeline = HttpResponsePipeline()

    private val backend: HttpClientBackend = backendFactory()

    init {
        requestPipeline.intercept(HttpRequestPipeline.Send, { requestData ->
            val request = when (requestData) {
                is HttpRequestDataBuilder -> requestData.build()
                else -> error("Unknown format of request: $requestData")
            }

            val response = backend.makeRequest(request)
            proceedWith(HttpCallData(request, response))
        })

        block()
    }

    override fun close() {
        backend.close()
    }
}

