package http.backend

import http.HttpCall
import http.request.makeRequest
import http.response.makeResponse
import org.jetbrains.ktor.http.HttpStatusCode

class HttpResponseData(
    val statusCode: HttpStatusCode,
    val body: String
)

suspend fun forceHttpResponseData(call: HttpCall): HttpResponseData {
    return call.makeResponse<HttpResponseData>(call.makeRequest()).value as HttpResponseData
}
