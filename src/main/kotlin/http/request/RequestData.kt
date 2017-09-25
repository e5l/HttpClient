package http.request

import org.jetbrains.ktor.http.HttpMethod

class RequestData(val url: Url, val method: HttpMethod, val headers: Headers)


class RequestDataBuilder() {

    constructor(data: RequestData) : this() {
        method = data.method
        url.takeFrom(data.url)
        headers.takeFrom(data.headers)
    }

    var method = HttpMethod.Get
    val url = UrlBuilder()
    val headers = HeadersBuilder()

    fun headers(block: HeadersBuilder.() -> Unit) = headers.apply(block)

    fun url(block: UrlBuilder.() -> Unit) = url.block()

    fun build(): RequestData = RequestData(url.build(), method, headers.build())
}

