package http.response

import http.call.HttpClientCall
import http.common.ProtocolVersion
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.http.response.HttpResponse
import org.jetbrains.ktor.util.ValuesMap
import org.jetbrains.ktor.util.ValuesMapBuilder

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
        override val headers: ValuesMap

) : HttpResponse

class ResponseDataBuilder {
    lateinit var statusCode: HttpStatusCode
    lateinit var reason: String
    lateinit var version: ProtocolVersion

    private val headersBuilder = ValuesMapBuilder()

    fun headers(block: ValuesMapBuilder.() -> Unit) {
        headersBuilder.apply(block)
    }

    fun build(): ResponseData = ResponseData(
            statusCode, reason, version, // status line
            headersBuilder.build()
    )
}

inline suspend fun <reified T> HttpClientCall.makeResponse(responseData: Any): ResponseContainer =
        response.pipeline.execute(this, ResponseContainer(T::class, responseData))
