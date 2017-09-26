package http.response

import http.common.ProtocolVersion
import http.request.Headers
import http.request.HeadersBuilder
import org.jetbrains.ktor.http.HttpStatusCode

data class Response(
        val statusCode: HttpStatusCode,
        val reason: String,
        val version: ProtocolVersion,
        val headers: Headers,
        val payload: Any
)

class ResponseBuilder() {

    constructor(response: Response) : this() {
        statusCode = response.statusCode
        reason = response.reason
        version = response.version
        headers.appendAll(response.headers)
        payload = response.payload
    }

    lateinit var statusCode: HttpStatusCode
    lateinit var reason: String
    lateinit var version: ProtocolVersion
    lateinit var payload: Any

    val headers = HeadersBuilder()

    fun headers(block: HeadersBuilder.() -> Unit) {
        headers.apply(block)
    }

    fun build(): Response = Response(statusCode, reason, version, headers.build(), payload)
}

