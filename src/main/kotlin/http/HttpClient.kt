package http

import http.backend.*
import http.pipeline.*
import http.request.*
import http.response.*

class HttpClient(
        backendFactory: HttpClientBackendFactory,
        block: HttpClient.() -> Unit = {}
) : CallScope(EmptyScope()) {
    override val requestPipeline = RequestPipeline()
    override val responsePipeline = ResponsePipeline()

    private val backend: HttpClientBackend = backendFactory()

    init {
        requestPipeline.intercept(RequestPipeline.Send) { requestData: Any ->
            val builder = ResponseDataBuilder()
            val responsePayload = backend.makeRequest(call.request.data, builder, requestData)

            call.response.prepare(builder.build())
            proceedWith(responsePayload)
        }

        block()
    }

    override fun close() {
        backend.close()
    }
}

