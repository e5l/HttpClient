package http.backend

import http.common.HttpMessageBody
import http.request.RequestData
import http.response.ResponseDataBuilder
import java.io.Closeable

interface HttpClientBackend : Closeable {
    suspend fun makeRequest(data: RequestData, builder: ResponseDataBuilder, requestPayload: Any): HttpMessageBody
}

interface HttpClientBackendFactory {
    operator fun invoke(): HttpClientBackend
}
