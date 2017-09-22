package http

import http.request.RequestDataBuilder
import org.jetbrains.ktor.http.util.URLProtocol


fun RequestDataBuilder.url(
        scheme: String = "http",
        host: String = "localhost",
        port: Int = 80,
        path: String = ""
) {
    url {
        this.host = host
        this.protocol = URLProtocol(scheme, port)
        this.port = port
        path(path)
    }
}
