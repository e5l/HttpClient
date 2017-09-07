package http.request

import http.call.HttpClientCall
import org.jetbrains.ktor.http.RequestConnectionPoint
import org.jetbrains.ktor.http.request.HttpRequest
import org.jetbrains.ktor.util.ValuesMap


class Request(
        val call: HttpClientCall,
        val pipeline: RequestPipeline,
        override val headers: ValuesMap,
        override val local: RequestConnectionPoint,
        override val queryParameters: ValuesMap
) : HttpRequest
