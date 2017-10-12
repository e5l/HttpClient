package http

import http.backend.HttpClientBackend
import http.backend.HttpClientBackendFactory
import http.call.HttpClientCall
import http.pipeline.*
import http.request.HttpRequestBuilder
import http.request.HttpRequestPipeline
import http.utils.safeAs

class HttpClient private constructor(val backend: HttpClientBackend) {
    companion object {
        operator fun invoke(backendFactory: HttpClientBackendFactory): HttpClientScope {
            val backend = backendFactory()

            return HttpCallScope(EmptyScope).config {
                install("backend") {
                    requestPipeline.intercept(HttpRequestPipeline.Send) { builder ->
                        val request = builder.safeAs<HttpRequestBuilder>()?.build() ?: return@intercept
                        val response = backend.makeRequest(request)
                        proceedWith(HttpClientCall(request, response.build(), context))
                    }
                }
            }.default()
        }
    }
}
