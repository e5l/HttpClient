package http.features

import http.pipeline.ClientScope
import http.request.RequestBuilder
import http.request.RequestPipeline
import http.response.ResponseBuilder
import http.response.ResponsePipeline
import http.utils.*
import org.jetbrains.ktor.cio.ByteBufferWriteChannel
import org.jetbrains.ktor.cio.toInputStream
import org.jetbrains.ktor.cio.toReadChannel
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.http.response.contentType
import org.jetbrains.ktor.http.withCharset
import org.jetbrains.ktor.util.AttributeKey
import java.io.InputStreamReader
import java.nio.charset.Charset

class PlainText(val defaultCharset: Charset) {

    fun read(response: ResponseBuilder): String? {
        val payload = response.payload.safeAs<HttpMessageBody>() ?: return null
        val charset = response.headers.charset() ?: defaultCharset

        return when (payload) {
            is WriteChannelBody -> {
                val channel = ByteBufferWriteChannel().apply(payload.block)
                channel.toString(charset)
            }
            is ReadChannelBody -> InputStreamReader(payload.channel.toInputStream(), charset).readText()
            is EmptyBody -> ""
        }
    }

    fun write(requestBuilder: RequestBuilder): HttpMessageBody? {
        val requestString = requestBuilder.payload.safeAs<String>() ?: return null
        val charset = requestBuilder.charset ?: defaultCharset
        val payload = requestString.toByteArray(charset)

        with(requestBuilder.headers) {
            get(HttpHeaders.ContentType) ?: contentType(ContentType.Text.Plain.withCharset(charset))
        }

        return ReadChannelBody(payload.toReadChannel())
    }

    class Configuration {
        var defaultCharset: Charset = Charset.defaultCharset()

        fun build(): PlainText = PlainText(defaultCharset)
    }

    companion object Feature : ClientFeature<Configuration, PlainText> {
        override val key = AttributeKey<PlainText>("PlainText")

        override fun prepare(configure: Configuration.() -> Unit): PlainText = Configuration().apply(configure).build()

        override fun install(feature: PlainText, scope: ClientScope) {
            scope.requestPipeline.intercept(RequestPipeline.Transform) { data ->
                val requestBuilder = data.safeAs<RequestBuilder>() ?: return@intercept
                requestBuilder.payload = feature.write(requestBuilder) ?: return@intercept
            }

            scope.responsePipeline.intercept(ResponsePipeline.Transform) { (expectedType, _, response) ->
                if (expectedType != String::class) {
                    return@intercept
                }

                response.payload = feature.read(response) ?: return@intercept
            }
        }
    }
}

