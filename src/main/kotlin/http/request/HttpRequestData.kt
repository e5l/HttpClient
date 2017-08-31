package http.request

import http.common.EmptyBody
import http.common.HttpMessageBody
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.util.URLBuilder
import org.jetbrains.ktor.util.URLProtocol
import org.jetbrains.ktor.util.ValuesMap
import org.jetbrains.ktor.util.ValuesMapBuilder

interface HttpRequestData {
    val protocol: URLProtocol
    val method: HttpMethod
    val url: String
    val headers: ValuesMap
    val body: HttpMessageBody
}

class HttpRequestDataBuilder {
    var protocol = URLProtocol.HTTP
    var method = HttpMethod.Get

    private val urlBuilder = URLBuilder()
    private val headersBuilder = ValuesMapBuilder()

    var body: HttpMessageBody = EmptyBody

    fun url(block: URLBuilder.() -> Unit) {
        urlBuilder.apply(block)
    }

    fun headers(block: ValuesMapBuilder.() -> Unit) {
        headersBuilder.apply(block)
    }

    fun build() =  object : HttpRequestData {
        override val protocol = this@HttpRequestDataBuilder.protocol
        override val method = this@HttpRequestDataBuilder.method
        override val url = urlBuilder.build()
        override val headers = headersBuilder.build()
        override val body = this@HttpRequestDataBuilder.body
    }
}
