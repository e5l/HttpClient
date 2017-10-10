package http.response

import http.utils.*
import org.jetbrains.ktor.http.HttpStatusCode
import java.util.*

data class Response(
        val statusCode: HttpStatusCode,
        val reason: String,
        val version: ProtocolVersion,
        val headers: Headers,
        val payload: Any,
        val requestTime: Date,
        val responseTime: Date
) {
    val cacheControl: ResponseCacheControl by lazy { headers.computeResponseCacheControl() }
}

class ResponseBuilder() {
    constructor(response: Response) : this() {
        statusCode = response.statusCode
        reason = response.reason
        version = response.version
        headers.appendAll(response.headers)
        payload = response.payload
        responseTime = response.responseTime
        requestTime = response.requestTime
    }

    lateinit var statusCode: HttpStatusCode
    lateinit var reason: String
    lateinit var version: ProtocolVersion
    lateinit var payload: Any
    lateinit var requestTime: Date
    lateinit var responseTime: Date

    val headers = HeadersBuilder()

    val cacheControl: ResponseCacheControl get() = headers.computeResponseCacheControl()

    fun headers(block: HeadersBuilder.() -> Unit) {
        headers.apply(block)
    }

    fun build(): Response = Response(statusCode, reason, version, valuesOf(headers), payload, requestTime, responseTime)
}
