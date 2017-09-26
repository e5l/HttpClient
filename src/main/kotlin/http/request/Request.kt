package http.request

import org.jetbrains.ktor.http.HttpMethod
import java.nio.charset.Charset


class Request(val url: Url, val method: HttpMethod, val headers: Headers, val payload: Any)


class RequestBuilder() {

    constructor(data: Request) : this() {
        method = data.method
        url.takeFrom(data.url)
        headers.appendAll(data.headers)
    }

    var method = HttpMethod.Get
    val url = UrlBuilder()
    val headers = HeadersBuilder()
    var payload: Any = Unit
    var charset: Charset = Charset.defaultCharset()

    fun headers(block: HeadersBuilder.() -> Unit) = headers.apply(block)

    fun url(block: UrlBuilder.() -> Unit) = url.block()

    fun build(): Request = Request(url.build(), method, headers.build(), payload)
}

