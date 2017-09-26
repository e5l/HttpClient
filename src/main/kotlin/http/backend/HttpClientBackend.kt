package http.backend

import http.request.Request
import http.response.ResponseBuilder
import java.io.Closeable

interface HttpClientBackend : Closeable {
    suspend fun makeRequest(data: Request): ResponseBuilder
}

interface HttpClientBackendFactory {
    operator fun invoke(): HttpClientBackend
}
