package http.backend

import http.request.HttpRequestData
import http.response.HttpResponseData
import java.io.Closeable

interface HttpBackend : Closeable {
    suspend fun makeRequest(data: HttpRequestData): HttpResponseData
}