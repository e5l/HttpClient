package http

import http.request.RequestDataBuilder
import org.jetbrains.ktor.util.URLBuilder

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