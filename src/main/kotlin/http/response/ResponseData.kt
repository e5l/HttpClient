package http.response

import http.common.ProtocolVersion
import org.jetbrains.ktor.http.HttpMessage
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.util.ValuesMapBuilder

interface ResponseData : HttpMessage {
    // status line
    val statusCode: HttpStatusCode
    val reason: String
    val version: ProtocolVersion
}

class ResponseBuilder {
    lateinit var statusCode: HttpStatusCode
    lateinit var reason: String
    lateinit var version: ProtocolVersion

    private val headersBuilder = ValuesMapBuilder()

    fun headers(block: ValuesMapBuilder.() -> Unit) {
        headersBuilder.apply(block)
    }

}
