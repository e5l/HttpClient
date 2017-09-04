package http

import http.request.HttpRequestDataBuilder
import org.jetbrains.ktor.util.URLBuilder

fun HttpRequestDataBuilder.url(
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