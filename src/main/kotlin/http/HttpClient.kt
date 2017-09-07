package http

import http.backend.HttpClientBackend
import http.backend.HttpClientBackendFactory
import http.pipeline.CallScope
import http.pipeline.EmptyScope
import http.request.RequestPipeline
import http.response.HttpResponsePipeline

class HttpClient(
        backendFactory: HttpClientBackendFactory,
        block: HttpClient.() -> Unit = {}
) : CallScope(EmptyScope()) {
    override val requestPipeline = RequestPipeline()
    override val responsePipeline = HttpResponsePipeline()

    private val backend: HttpClientBackend = backendFactory()

    init {
        requestPipeline.intercept(RequestPipeline.Send) { requestData: Any ->
            val response = backend.makeRequest(call.request, call.response.builder, requestData)
            proceedWith(response)
        }

        block()
    }

    override fun close() {
        backend.close()
    }
}
