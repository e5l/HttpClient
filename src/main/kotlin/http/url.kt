package http

import http.request.RequestDataBuilder
import org.jetbrains.ktor.util.URLBuilder
import org.jetbrains.ktor.util.URLProtocol

fun RequestDataBuilder.url(
        host: String = "localhost",
        port: Int = 80,
        path: String = ""
) {
    url {
        this.host = host
        this.port = port
        path(path)
    }

}

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
