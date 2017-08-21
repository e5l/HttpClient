package http.backend

import java.io.Closeable

interface HttpBackend : Closeable {
    suspend fun makeRequest(data: HttpRequestData): HttpResponseData
}