package http.features

import http.common.EmptyBody
import http.common.HttpMessageBody
import http.common.ReadChannelBody
import http.common.WriteChannelBody
import http.pipeline.ClientScope
import http.request.RequestPipeline
import http.request.charset
import http.response.ResponsePipeline
import http.utils.safeAs
import org.jetbrains.ktor.cio.ByteBufferWriteChannel
import org.jetbrains.ktor.cio.toInputStream
import org.jetbrains.ktor.cio.toReadChannel
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.util.AttributeKey
import java.io.InputStreamReader
import java.nio.charset.Charset

class PlainText(val config: Configuration) {

    class Configuration {
        var defaultCharset = Charset.defaultCharset()
    }

    companion object Feature : ClientScopeFeature<Configuration, PlainText> {
        override val key = AttributeKey<PlainText>("PlainText")

        override fun install(scope: ClientScope, configure: Configuration.() -> Unit): PlainText {
            val config = Configuration().apply(configure)
            scope.responsePipeline.intercept(ResponsePipeline.Transform) { container ->
                val payload = container.response.safeAs<HttpMessageBody>() ?: return@intercept
                if (container.expectedType != String::class) {
                    return@intercept
                }

                val charset = call.response.data.headers.charset() ?: config.defaultCharset
                val body = when (payload) {
                    is WriteChannelBody -> {
                        val channel = ByteBufferWriteChannel().apply(payload.block)
                        channel.toString(charset)
                    }
                    is ReadChannelBody -> InputStreamReader(payload.channel.toInputStream(), charset).readText()
                    is EmptyBody -> ""
                }

                proceedWith(container.copy(response = body))
            }

            scope.requestPipeline.intercept(RequestPipeline.Content) { requestData ->
                val requestString = requestData.safeAs<String>() ?: return@intercept

                val charset = call.request.data.headers.charset() ?: config.defaultCharset
                val payload = requestString.toByteArray(charset)

                with(call.request.data.headers) {
                    get(HttpHeaders.ContentType) ?: TODO() // contentType(ContentType.Text.Plain.withCharset(charset))
                }

                proceedWith(ReadChannelBody(payload.toReadChannel()))
            }

            return PlainText(config)
        }
    }
}

