package http.request

import http.call.HttpClientCall
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.http.RequestConnectionPoint
import org.jetbrains.ktor.util.URLBuilder
import org.jetbrains.ktor.util.ValuesMapBuilder


class RequestBuilder {
    var scheme: String = "http"
    var method = HttpMethod.Get
    val version: String = ""

    val url = URLBuilder()
    val headers = ValuesMapBuilder()

    fun headers(block: ValuesMapBuilder.() -> Unit) {
        headers.apply(block)
    }

    fun url(block: URLBuilder.() -> Unit) {
        url.apply(block)
    }

    private val queryParameters = ValuesMapBuilder()

    fun build(call: HttpClientCall, pipeline: RequestPipeline): Request = Request(
            call, pipeline, headers.build(), object : RequestConnectionPoint {
        override val host: String = url.host
        override val method: HttpMethod = this@RequestBuilder.method
        override val port: Int = url.port
        override val remoteHost: String = url.host
        override val scheme: String = this@RequestBuilder.scheme
        override val uri: String = url.encodedPath
        override val version: String = this@RequestBuilder.version
    }, queryParameters.build()
    )
}

fun request(block: RequestBuilder.() -> Unit): RequestBuilder = RequestBuilder().apply(block)
