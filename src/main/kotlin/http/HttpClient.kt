package http

import http.backend.HttpClientBackend
import http.backend.HttpClientBackendFactory
import http.pipeline.EmptyScope
import http.pipeline.HttpClientScope
import http.request.HttpRequestData
import http.request.HttpRequestDataBuilder
import http.request.HttpRequestPipeline
import http.response.HttpResponsePipeline
import java.io.Closeable

class HttpClient(
        backendFactory: HttpClientBackendFactory,
        block: HttpClient.() -> Unit = {}
) : HttpClientScope, Closeable {

    override val parent: HttpClientScope = EmptyScope
    override val requestPipeline = HttpRequestPipeline()
    override val responsePipeline = HttpResponsePipeline()

    private val backend: HttpClientBackend = backendFactory()

    init {
        requestPipeline.intercept(HttpRequestPipeline.Send, { requestData ->
            val data = when (requestData) {
                is HttpRequestData -> requestData
                is HttpRequestDataBuilder -> requestData.build()
                else -> error("Unknown format of request: $requestData")
            }

            proceedWith(backend.makeRequest(data))
        })

        block()
    }

    override fun close() {
        backend.close()
    }
}

