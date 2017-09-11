package http.backend.jvm

import http.backend.HttpClientBackend
import http.backend.HttpClientBackendFactory
import http.common.*
import http.request.RequestData
import http.response.ResponseDataBuilder
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import org.apache.http.HttpResponse
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.client.utils.URIBuilder
import org.apache.http.concurrent.FutureCallback
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.entity.InputStreamEntity
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.jetbrains.ktor.cio.ByteBufferWriteChannel
import org.jetbrains.ktor.cio.toInputStream
import org.jetbrains.ktor.cio.toReadChannel
import org.jetbrains.ktor.http.HttpStatusCode


class ApacheBackend : HttpClientBackend {
    private val backend = HttpAsyncClients.createDefault()

    init {
        backend.start()
    }

    suspend override fun makeRequest(data: RequestData, builder: ResponseDataBuilder, requestPayload: Any): HttpMessageBody {
        val apacheBuilder = RequestBuilder.create(data.local.method.value)
        with(data) {
            apacheBuilder.uri = URIBuilder().apply {
                scheme = local.scheme
                host = local.host
                port = local.port
                path = local.uri
            }.build()
        }

        data.headers.entries().forEach { (name, values) ->
            values.forEach { value -> apacheBuilder.addHeader(name, value) }
        }

        when (requestPayload) {
            is ReadChannelBody -> InputStreamEntity(requestPayload.channel.toInputStream())
            is WriteChannelBody -> {
                val channel = ByteBufferWriteChannel()
                requestPayload.block(channel)
                ByteArrayEntity(channel.toByteArray())
            }
            else -> null
        }?.let { apacheBuilder.entity = it }

        val apacheRequest = apacheBuilder.build()

        val response = suspendCancellableCoroutine<HttpResponse> { continuation ->
            backend.execute(apacheRequest, object : FutureCallback<HttpResponse> {
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

        builder.apply {
            statusCode = HttpStatusCode.fromValue(statusLine.statusCode)
            reason = statusLine.reasonPhrase

            headers {
                response.allHeaders.forEach { append(it.name, it.value) }
            }

            with(protocolVersion) {
                version = ProtocolVersion(protocol, major, minor)
            }
        }

        return if (entity.isStreaming) ReadChannelBody(entity.content.toReadChannel()) else EmptyBody
    }

    override fun close() {
        backend.close()
    }

    companion object : HttpClientBackendFactory {
        override operator fun invoke(): HttpClientBackend = ApacheBackend()
    }
}

