package http.response

import http.call.HttpClientCall
import http.common.ProtocolVersion
import http.request.Headers
import http.request.HeadersBuilder
import org.jetbrains.ktor.http.HttpStatusCode

class Response(val call: HttpClientCall, val pipeline: ResponsePipeline) {
    lateinit var data: ResponseData

    fun prepare(data: ResponseData) {
        this.data = data
    }
}

class ResponseData(
        val statusCode: HttpStatusCode,
        val reason: String,
        val version: ProtocolVersion,
        val headers: Headers
)

class ResponseDataBuilder {
    lateinit var statusCode: HttpStatusCode
    lateinit var reason: String
    lateinit var version: ProtocolVersion

    private val headersBuilder = HeadersBuilder()

    fun headers(block: HeadersBuilder.() -> Unit) {
        headersBuilder.apply(block)
    }

    fun build(): ResponseData = ResponseData(statusCode, reason, version, headersBuilder.build())
}

