package http.request

import http.utils.*
import org.jetbrains.ktor.http.HttpMethod
import java.nio.charset.Charset


class HttpRequest(val url: Url, val method: HttpMethod, val headers: Headers, val payload: Any) {
    val cacheControl: HttpRequestCacheControl by lazy { headers.computeRequestCacheControl() }
}

class HttpRequestBuilder() {
    constructor(data: HttpRequest) : this() {
        method = data.method
        url.takeFrom(data.url)
        headers.appendAll(data.headers)
    }

    var method = HttpMethod.Get
    val url = UrlBuilder()
    val headers = HeadersBuilder()
    var payload: Any = Unit
    var charset: Charset? = null

    val cacheControl: HttpRequestCacheControl get() = headers.computeRequestCacheControl()

    fun headers(block: HeadersBuilder.() -> Unit) = headers.apply(block)

    fun url(block: UrlBuilder.() -> Unit) = url.block()

    fun build(): HttpRequest = HttpRequest(url.build(), method, valuesOf(headers), payload)
}

