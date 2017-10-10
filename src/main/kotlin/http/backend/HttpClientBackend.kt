package http.backend

import http.request.HttpRequest
import http.response.HttpResponseBuilder
import java.io.Closeable

interface HttpClientBackend : Closeable {
    suspend fun makeRequest(data: HttpRequest): HttpResponseBuilder
}

interface HttpClientBackendFactory {
    operator fun invoke(): HttpClientBackend
}
