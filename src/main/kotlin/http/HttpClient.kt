package http

import http.backend.HttpClientBackend
import http.backend.HttpClientBackendFactory
import http.call.HttpClientCall
import http.pipeline.CallScope
import http.pipeline.ClientScope
import http.pipeline.config
import http.pipeline.default
import http.request.RequestBuilder
import http.request.RequestPipeline
import http.utils.safeAs

class HttpClient private constructor(val backend: HttpClientBackend) : CallScope() {

    override fun close() {
        backend.close()
    }

    companion object {
        operator fun invoke(backendFactory: HttpClientBackendFactory): ClientScope {
            val backend = backendFactory()

            return HttpClient(backend).config {
                install("backend") {
                    requestPipeline.intercept(RequestPipeline.Send) { requestBuilder ->
                        val request = requestBuilder.safeAs<RequestBuilder>()?.build()
                                ?: error("Subject in request pipeline is not RequestDataBuilder: $requestBuilder")

                        val response = backend.makeRequest(request)
                        proceedWith(HttpClientCall(request, response.build(), call))
                    }
                }
            }.default()
        }
    }
}
