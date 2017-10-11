package http.features

import http.pipeline.HttpClientScope
import http.pipeline.intercept
import http.request.HttpRequestBuilder
import http.request.HttpRequestPipeline
import http.response.HttpResponseBuilder
import http.response.HttpResponsePipeline
import http.utils.*
import io.ktor.cio.ByteBufferWriteChannel
import io.ktor.cio.toInputStream
import io.ktor.cio.toReadChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.response.contentType
import io.ktor.http.withCharset
import io.ktor.util.AttributeKey
import java.io.InputStreamReader
import java.nio.charset.Charset

class HttpPlainText(val defaultCharset: Charset) {

    fun read(response: HttpResponseBuilder): String? {
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

    fun write(requestBuilder: HttpRequestBuilder): HttpMessageBody? {
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

        fun build(): HttpPlainText = HttpPlainText(defaultCharset)
    }

    companion object Feature : HttpClientFeature<Configuration, HttpPlainText> {
        override val key = AttributeKey<HttpPlainText>("HttpPlainText")

        override fun prepare(block: Configuration.() -> Unit): HttpPlainText = Configuration().apply(block).build()

        override fun install(feature: HttpPlainText, scope: HttpClientScope) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Transform) { builder: HttpRequestBuilder ->
                builder.payload = feature.write(builder) ?: return@intercept
            }

            scope.responsePipeline.intercept(HttpResponsePipeline.Transform) { (expectedType, _, response) ->
                if (expectedType != String::class) {
                    return@intercept
                }

                response.payload = feature.read(response) ?: return@intercept
            }
        }
    }
}

