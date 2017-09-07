package http.backend

import http.common.HttpMessageBody
import http.request.Request
import http.response.ResponseBuilder
import java.io.Closeable

interface HttpClientBackend : Closeable {
    suspend fun makeRequest(data: Request, builder: ResponseBuilder, requestData: Any): HttpMessageBody
}

interface HttpClientBackendFactory {
    operator fun invoke(): HttpClientBackend
}
