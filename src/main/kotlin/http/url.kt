package http

import http.request.RequestDataBuilder


fun RequestDataBuilder.url(
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
