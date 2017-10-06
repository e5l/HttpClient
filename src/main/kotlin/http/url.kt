package http

import http.request.RequestBuilder
import http.utils.ParametersBuilder
import http.utils.Url
import http.utils.UrlBuilder
import java.net.URL


fun RequestBuilder.url(
        scheme: String = "http",
        host: String = "localhost",
        port: Int = 80,
        path: String = ""
) {
    url.apply {
        this.scheme = scheme
        this.host = host
        this.port = port
        this.path = path
    }
}

fun RequestBuilder.url(data: Url) {
    url.takeFrom(data)
}

fun UrlBuilder.takeFrom(url: Url): UrlBuilder {
    scheme = url.scheme
    host = url.host
    port = url.port
    path = url.path
    username = url.username
    password = url.password
    queryParameters = ParametersBuilder().apply {
        appendAll(url.queryParameters)
    }

    return this
}

fun UrlBuilder.takeFrom(data: URL) {
    scheme = data.protocol
    host = data.host
    path = data.path
    port = if (scheme == "https") 443 else 80

    // TODO: parse query parameters
}

fun UrlBuilder.takeFrom(url: String) = takeFrom(URL(url))
