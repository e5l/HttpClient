package http.backend.jvm

import http.backend.HttpBackend
import http.core.ProtocolVersion
import http.request.HttpRequestData
import http.response.EmptyBody
import http.response.HttpResponseData
import http.response.HttpResponseDataBuilder
import http.response.StreamBody
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import org.apache.http.HttpResponse
import org.apache.http.concurrent.FutureCallback
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.jetbrains.ktor.http.HttpStatusCode
import java.net.URI

class ApacheBackend : HttpBackend {
    private val backend = HttpAsyncClients.createDefault()

    init {
        backend.start()
    }

    suspend override fun makeRequest(data: HttpRequestData): HttpResponseData {
        val builder = org.apache.http.client.methods.RequestBuilder.create(data.method.value)
        builder.uri = URI(data.url)

        data.headers.entries().forEach { (name, values) ->
            values.forEach { value -> builder.addHeader(name, value) }
        }

        val request = builder.build()

        val response = suspendCancellableCoroutine<HttpResponse> { continuation ->
            backend.execute(request, object : FutureCallback<HttpResponse> {
                override fun failed(exception: Exception) {
                    continuation.resumeWithException(exception)
                }

                override fun cancelled() {
                    continuation.cancel()
                }

                override fun completed(result: HttpResponse) {
                    continuation.resume(result)
                }
            })
        }

        val protocolVersion = response.statusLine.protocolVersion

        return HttpResponseDataBuilder().apply {
            statusCode = HttpStatusCode.fromValue(response.statusLine.statusCode)
            body = if (response.entity.isStreaming) StreamBody(response.entity.content) else EmptyBody
            reason = response.statusLine.reasonPhrase

            headers {
                response.allHeaders.forEach { append(it.name, it.value) }
            }

            with(protocolVersion) {
                version = ProtocolVersion(protocol, major, minor)
            }
        }.build()
    }

    override fun close() {
        backend.close()
    }
}