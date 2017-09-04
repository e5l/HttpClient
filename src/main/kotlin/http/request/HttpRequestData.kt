package http.request

import http.common.EmptyBody
import http.common.HttpMessage
import http.common.HttpMessageBody
import http.common.ReadChannelBody
import org.jetbrains.ktor.cio.ByteBufferReadChannel
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.util.URLBuilder
import org.jetbrains.ktor.util.URLProtocol
import org.jetbrains.ktor.util.ValuesMapBuilder

interface HttpRequestData : HttpMessage {
    val protocol: URLProtocol
    val method: HttpMethod
    val url: URLBuilder
}

class HttpRequestDataBuilder {
    var protocol = URLProtocol.HTTP
    var method = HttpMethod.Get

    val url = URLBuilder()
    val headersBuilder = ValuesMapBuilder()

    var body: HttpMessageBody = EmptyBody

    fun headers(block: ValuesMapBuilder.() -> Unit) {
        headersBuilder.apply(block)
    }

    fun url(block: URLBuilder.() -> Unit) {
        url.apply(block)
    }

    fun build() =  object : HttpRequestData {
        override val protocol = this@HttpRequestDataBuilder.protocol
        override val method = this@HttpRequestDataBuilder.method
        override val url = this@HttpRequestDataBuilder.url
        override val headers = headersBuilder.build()
        override val body = this@HttpRequestDataBuilder.body
    }
}

fun request(block: HttpRequestDataBuilder.() -> Unit): HttpRequestDataBuilder =
        HttpRequestDataBuilder().apply(block)

fun HttpRequestDataBuilder.body(text: String) {
    body = ReadChannelBody(ByteBufferReadChannel(text.toByteArray()))
}
