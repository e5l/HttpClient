package http.backend.jvm

import http.backend.HttpClientBackend
import http.backend.HttpClientBackendFactory
import http.request.Request
import http.response.ResponseBuilder
import http.utils.EmptyBody
import http.utils.ProtocolVersion
import http.utils.ReadChannelBody
import http.utils.WriteChannelBody
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import org.apache.http.HttpResponse
import org.apache.http.client.config.CookieSpecs
import org.apache.http.client.config.RequestConfig
import org.apache.http.client.methods.RequestBuilder
import org.apache.http.client.utils.URIBuilder
import org.apache.http.concurrent.FutureCallback
import org.apache.http.entity.ByteArrayEntity
import org.apache.http.entity.InputStreamEntity
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.jetbrains.ktor.cio.ByteBufferWriteChannel
import org.jetbrains.ktor.cio.toInputStream
import org.jetbrains.ktor.cio.toReadChannel
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.util.flattenEntries
import java.util.*


class ApacheBackend : HttpClientBackend {
    private val backend: CloseableHttpAsyncClient

    init {
        val config = RequestConfig.custom().setCookieSpec(CookieSpecs.IGNORE_COOKIES).build()
        backend = HttpAsyncClients.custom().setDefaultRequestConfig(config).build()
        backend.start()
    }

    suspend override fun makeRequest(data: Request): ResponseBuilder {
        val apacheBuilder = RequestBuilder.create(data.method.value)
        with(data) {
            apacheBuilder.uri = URIBuilder().apply {
                scheme = url.scheme
                host = url.host
                port = url.port
                path = url.path
                url.queryParameters.flattenEntries().forEach { (key, value) -> addParameter(key, value) }
            }.build()
        }

        data.headers.entries().forEach { (name, values) ->
            values.forEach { value -> apacheBuilder.addHeader(name, value) }
        }

        val requestPayload = data.payload
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

        val startTime = Date()
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

        val builder = ResponseBuilder()
        builder.apply {
            statusCode = HttpStatusCode.fromValue(statusLine.statusCode)
            reason = statusLine.reasonPhrase
            requestTime = startTime
            responseTime = Date()

            headers {
                response.allHeaders.forEach { headerLine ->
                    headerLine.elements.forEach line@ {
                        append(headerLine.name, it.toString())
                    }
                }
            }

            with(statusLine.protocolVersion) {
                version = ProtocolVersion(protocol, major, minor)
            }

            payload = if (entity.isStreaming) ReadChannelBody(entity.content.toReadChannel()) else EmptyBody
        }

        return builder
    }

    override fun close() {
        backend.close()
    }

    companion object : HttpClientBackendFactory {
        override operator fun invoke(): HttpClientBackend = ApacheBackend()
    }
}

