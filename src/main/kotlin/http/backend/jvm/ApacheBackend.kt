package http.backend.jvm

import http.backend.HttpBackend
import http.common.ProtocolVersion
import http.request.HttpRequestData
import http.common.EmptyBody
import http.common.ReadChannelBody
import http.common.WriteChannelBody
import http.response.HttpResponseData
import http.response.HttpResponseDataBuilder
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import org.apache.http.HttpResponse
import org.apache.http.concurrent.FutureCallback
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.entity.InputStreamEntity
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.jetbrains.ktor.cio.ByteBufferWriteChannel
import org.jetbrains.ktor.cio.toInputStream
import org.jetbrains.ktor.cio.toReadChannel
import org.jetbrains.ktor.http.HttpStatusCode
import java.net.URI

// make as factory
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

        val requestBody = data.body
        when (requestBody) {
            is ReadChannelBody -> InputStreamEntity(requestBody.channel.toInputStream())
            is WriteChannelBody -> {
                val channel = ByteBufferWriteChannel()
                requestBody.block(channel)
                ByteArrayEntity(channel.toByteArray())
            }
            else -> null
        }?.let { builder.entity = it }

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

        val statusLine = response.statusLine
        val entity = response.entity
        val protocolVersion = statusLine.protocolVersion

        return HttpResponseDataBuilder().apply {
            statusCode = HttpStatusCode.fromValue(statusLine.statusCode)
            body = if (entity.isStreaming) ReadChannelBody(entity.content.toReadChannel()) else EmptyBody
            reason = statusLine.reasonPhrase

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
