package http

import http.backend.*
import http.features.ClientScopeFeature
import http.features.IgnoreBody
import http.features.PlainText
import http.features.install
import http.pipeline.*
import http.request.*
import http.response.*

class HttpClient(
        backendFactory: HttpClientBackendFactory,
        block: HttpClient.() -> Unit = {},
        defaultFeatures: List<ClientScopeFeature<Any, out Any>> = listOf(PlainText, IgnoreBody)
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

        defaultFeatures.forEach {
            install(it)
        }
    }

    override fun close() {
        backend.close()
    }
}
