package http.features

import http.bodyText
import http.common.HttpMessageBody
import http.common.ReadChannelBody
import http.pipeline.ClientScope
import http.request.RequestPipeline
import http.response.ResponsePipeline
import org.jetbrains.ktor.cio.toReadChannel
import org.jetbrains.ktor.http.request.contentCharset
import org.jetbrains.ktor.util.AttributeKey
import safeAs
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
                if (container.expectedType != String::class)  {
                    return@intercept
                }

                val body = payload.bodyText(call.response.data.contentCharset() ?: config.defaultCharset)
                proceedWith(container.copy(response = body))
            }

            scope.requestPipeline.intercept(RequestPipeline.Content) { requestData ->
                if (requestData !is String) {
                    return@intercept
                }

                val charset = call.request.data.contentCharset() ?: config.defaultCharset
                val payload = requestData.toByteArray(charset).toReadChannel()
                proceedWith(ReadChannelBody(payload))
            }

            return PlainText(config)
        }
    }
}