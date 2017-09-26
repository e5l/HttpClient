package http.features

import http.common.EmptyBody
import http.common.HttpMessageBody
import http.common.ReadChannelBody
import http.common.WriteChannelBody
import http.pipeline.ClientScope
import http.request.RequestBuilder
import http.request.RequestPipeline
import http.request.charset
import http.response.ResponsePipeline
import http.utils.safeAs
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

class PlainText(val defaultCharset

: Charset) {

    class Configuration {
        var defaultCharset: Charset = Charset.defaultCharset()

        fun build(): PlainText = PlainText(defaultCharset)
    }

    companion object Feature : ClientFeature<Configuration, PlainText> {
        override val key = AttributeKey<PlainText>("PlainText")

        override fun prepare(configure: Configuration.() -> Unit): PlainText = Configuration().apply(configure).build()


        override fun install(feature: PlainText, scope: ClientScope) {
            scope.requestPipeline.intercept(RequestPipeline.Content) { data ->
                val requestData = data.safeAs<RequestBuilder>() ?: return@intercept
                val requestString = requestData.payload.safeAs<String>() ?: return@intercept

                val charset = requestData.charset
                val payload = requestString.toByteArray(charset)

                with(requestData.headers) {
                    get(HttpHeaders.ContentType) ?: contentType(ContentType.Text.Plain.withCharset(charset))
                }

                requestData.payload = ReadChannelBody(payload.toReadChannel())
            }

            scope.responsePipeline.intercept(ResponsePipeline.Transform) { (expectedType, _, response) ->
                if (expectedType != String::class) {
                    return@intercept
                }

                val payload = response.payload.safeAs<HttpMessageBody>() ?: return@intercept
                val charset = response.headers.charset() ?: feature.defaultCharset

                response.payload = when (payload) {
                    is WriteChannelBody -> {
                        val channel = ByteBufferWriteChannel().apply(payload.block)
                        channel.toString(charset)
                    }
                    is ReadChannelBody -> InputStreamReader(payload.channel.toInputStream(), charset).readText()
                    is EmptyBody -> ""
                }
            }
        }
    }
}

