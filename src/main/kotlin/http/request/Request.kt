package http.request

import http.call.HttpClientCall
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.http.RequestConnectionPoint
import org.jetbrains.ktor.http.request.HttpRequest
import org.jetbrains.ktor.http.util.URLBuilder
import org.jetbrains.ktor.util.ValuesMap
import org.jetbrains.ktor.util.ValuesMapBuilder


class Request(
        val call: HttpClientCall,
        val pipeline: RequestPipeline
) {
    val data: RequestData by lazy { call.requestBuilder.build() }
}

class RequestData(
        override val headers: ValuesMap,
        val remote: RequestConnectionPoint,
        override val queryParameters: ValuesMap
) : HttpRequest {
    override val local: RequestConnectionPoint
        get() = remote
}

class RequestDataBuilder {
    var method = HttpMethod.Get
    val version: String = ""

    val url = URLBuilder()
    val headers = ValuesMapBuilder()

    fun headers(block: ValuesMapBuilder.() -> Unit) {
        headers.apply(block)
    }

    fun url(block: URLBuilder.() -> Unit) {
        url.block()
    }

    fun build(): RequestData = RequestData(
            headers.build(),
            object : RequestConnectionPoint {
                override val host: String = url.host
                override val method: HttpMethod = this@RequestDataBuilder.method
                override val port: Int = url.port
                override val remoteHost: String = url.host
                override val scheme: String = url.protocol.name
                override val uri: String = url.encodedPath
                override val version: String = this@RequestDataBuilder.version
            },
            url.parameters.build()
    )
}

fun request(block: RequestDataBuilder.() -> Unit): RequestDataBuilder = RequestDataBuilder().apply(block)

