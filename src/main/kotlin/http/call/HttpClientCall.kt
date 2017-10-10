package http.call

import http.pipeline.HttpClientScope
import http.request.*
import http.response.*
import kotlin.reflect.KClass


class HttpClientCall(
        val request: HttpRequest,
        val response: HttpResponse,
        private val scope: HttpClientScope
) {
    suspend fun receive(expectedType: KClass<*> = Unit::class): HttpResponseContainer {
        val subject = HttpResponseContainer(expectedType, request, HttpResponseBuilder(response))
        val container = scope.responsePipeline.execute(scope, subject)

        assert(container.response.payload::class == expectedType)
        return container
    }
}

suspend fun HttpClientScope.call(builder: HttpRequestBuilder): HttpClientCall =
        requestPipeline.execute(this, builder) as HttpClientCall

suspend fun HttpClientScope.call(block: HttpRequestBuilder.() -> Unit): HttpClientCall =
        call(HttpRequestBuilder().apply(block))

suspend inline fun <reified T> HttpClientCall.receive(): T = receive(T::class).response.payload as T

