package http

import http.request.RequestBuilder
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

fun UrlBuilder.takeFrom(data: URL) {
    scheme = data.protocol
    host = data.host
    path = data.path
    port = data.port
    // TODO: parse query parameters
}