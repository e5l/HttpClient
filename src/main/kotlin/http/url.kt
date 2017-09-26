package http

import http.request.RequestBuilder


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
