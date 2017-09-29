package http.call

import http.pipeline.ClientScope
import http.request.Request
import http.request.RequestBuilder
import http.response.Response
import http.response.ResponseBuilder
import http.response.ResponseContainer
import kotlin.reflect.KClass


class HttpClientCall(
        val request: Request,
        val response: Response,
        private val scope: ClientScope
) {
    suspend fun receive(expectedType: KClass<*> = Unit::class): ResponseContainer {
        val subject = ResponseContainer(expectedType, request, ResponseBuilder(response))
        val container = scope.responsePipeline.execute(scope, subject)

        assert(container.response.payload::class == expectedType)
        return container
    }
}

suspend fun ClientScope.call(builder: RequestBuilder): HttpClientCall =
        requestPipeline.execute(this, builder) as HttpClientCall

suspend fun ClientScope.call(block: RequestBuilder.() -> Unit): HttpClientCall =
        call(RequestBuilder().apply(block))

suspend inline fun <reified T> HttpClientCall.receive(): T = receive(T::class).response.payload as T

