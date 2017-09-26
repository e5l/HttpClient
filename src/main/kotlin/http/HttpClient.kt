package http

import http.backend.HttpClientBackend
import http.backend.HttpClientBackendFactory
import http.call.HttpClientCall
import http.features.ClientScopeFeature
import http.features.IgnoreBody
import http.features.PlainText
import http.features.install
import http.pipeline.CallScope
import http.pipeline.EmptyScope
import http.request.RequestBuilder
import http.request.RequestPipeline
import http.response.ResponsePipeline
import http.utils.safeAs

class HttpClient(
        backendFactory: HttpClientBackendFactory,
        block: HttpClient.() -> Unit = {},
        defaultFeatures: List<ClientScopeFeature<Any, out Any>> = listOf(PlainText, IgnoreBody)
) : CallScope(EmptyScope()) {
    override val requestPipeline = RequestPipeline()
    override val responsePipeline = ResponsePipeline()

    private val backend: HttpClientBackend = backendFactory()

    init {
        requestPipeline.intercept(RequestPipeline.Send) { requestBuilder ->
            val request =
                    requestBuilder.safeAs<RequestBuilder>()?.build()
                    ?: error("Subject in request pipeline is not RequestDataBuilder: $requestBuilder")

            val response = backend.makeRequest(request)

            proceedWith(HttpClientCall(request, response.build(), call))
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
