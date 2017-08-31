package http.backend

import http.request.HttpRequestData
import http.response.HttpResponseData
import java.io.Closeable

interface HttpClientBackend : Closeable {
    suspend fun makeRequest(data: HttpRequestData): HttpResponseData
}

interface HttpClientBackendFactory {
    operator fun invoke(): HttpClientBackend
}
