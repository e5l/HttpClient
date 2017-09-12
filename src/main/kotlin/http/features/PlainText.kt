package http.features

import http.bodyText
import http.common.ReadChannelBody
import http.pipeline.ClientScope
import http.request.RequestPipeline
import http.response.ResponsePipeline
import org.jetbrains.ktor.cio.toReadChannel
import org.jetbrains.ktor.util.AttributeKey
import java.nio.charset.Charset

class PlainText {
    class Configuration

    companion object Feature : ClientScopeFeature<Configuration, PlainText> {
        override val key = AttributeKey<PlainText>("PlainText")

        override fun install(scope: ClientScope, configure: Configuration.() -> Unit): PlainText {
            scope.responsePipeline.intercept(ResponsePipeline.Transform) { container ->
                if (container.expectedType != String::class) {
                    return@intercept
                }

                val body = call.bodyText()
                proceedWith(container.copy(response = body))
            }

            scope.requestPipeline.intercept(RequestPipeline.Content) { requestData ->
                if (requestData !is String) {
                    return@intercept
                }

                // extract charset from headers
                proceedWith(ReadChannelBody(requestData.toByteArray(Charset.defaultCharset()).toReadChannel()))
            }

            return PlainText()
        }
    }
}