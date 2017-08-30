package http.response

import http.HttpCall
import http.core.ProtocolVersion
import http.request.makeRequest
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.util.ValuesMap
import org.jetbrains.ktor.util.ValuesMapBuilder

interface HttpResponseData {
    // status line
    val statusCode: HttpStatusCode
    val reason: String
    val version: ProtocolVersion

    val body: ResponseBody
    val headers: ValuesMap
}

class HttpResponseDataBuilder() {
    lateinit var statusCode: HttpStatusCode
    lateinit var reason: String
    lateinit var version: ProtocolVersion

    lateinit var body: ResponseBody

    private val headersBuilder = ValuesMapBuilder()

    fun headers(block: ValuesMapBuilder.() -> Unit) {
        headersBuilder.apply(block)
    }

    fun build(): HttpResponseData = object : HttpResponseData {
        override val statusCode = this@HttpResponseDataBuilder.statusCode
        override val reason = this@HttpResponseDataBuilder.reason
        override val version = this@HttpResponseDataBuilder.version
        override val body = this@HttpResponseDataBuilder.body
        override val headers = headersBuilder.build()
    }
}

inline suspend fun <reified T> execute(call: HttpCall): T = call.makeResponse<T>(call.makeRequest()).value as? T
        ?: error("Fail to process call: $call \n" + "Expected type: ${T::class}")
