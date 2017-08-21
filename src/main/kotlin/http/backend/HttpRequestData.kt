package http.backend

import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.util.ValuesMap

class HttpScheme(val value: String) {
    companion object {
        val Http = HttpScheme("http")
        val Https = HttpScheme("https")
    }
}

class HttpRequestData(
        val scheme: HttpScheme,
        val method: HttpMethod,
        val url: String,
        val path: String,
        val headers: ValuesMap,
        val port: Short
) {
    class Builder {
        var scheme: HttpScheme = HttpScheme.Http
        var method: HttpMethod = HttpMethod.Get
        lateinit var url: String
        var path: String = ""
        var headers = ValuesMap.Empty
        var port: Short = 80

        fun build() =  HttpRequestData(scheme, method, url, path, headers, port)
    }
}
