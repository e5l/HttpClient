package http

import http.request.RequestDataBuilder
import org.jetbrains.ktor.util.URLBuilder
import org.jetbrains.ktor.util.URLProtocol

fun RequestDataBuilder.url(
        host: String = "localhost",
        port: Int = 80,
        path: String = "",
        additionalSettings: URLBuilder.() -> Unit = {}
) {
    url {
        this.host = host
        this.port = port
        path(path)
    }

    url(additionalSettings)
}

fun RequestDataBuilder.url(
        host: String = "localhost",
        protocol: URLProtocol,
        path: String = "",
        additionalSettings: URLBuilder.() -> Unit = {}
) {
    url {
        this.host = host
        this.port = port
        this.protocol = protocol
        path(path)
    }

    url(additionalSettings)
}
