package http.features

import http.bodyText
import http.charset
import http.common.HttpMessageBody
import http.pipeline.ClientScope
import http.response.HttpResponsePipeline
import org.jetbrains.ktor.util.AttributeKey

inline fun <reified T> Any?.safeAs(): T? = this as? T

class PlainText {
    class Configuration

    companion object Feature : ClientScopeFeature<Configuration, PlainText> {
        override val key = AttributeKey<PlainText>("PlainText")

        override fun install(scope: ClientScope, configure: Configuration.() -> Unit): PlainText {
            scope.responsePipeline.intercept(HttpResponsePipeline.Transform) { container ->
                if (container.expectedType != String::class) {
                    return@intercept
                }

                val body = call.bodyText()
//                        container.response.safeAs<HttpMessageBody>()?.bodyText(call.response.charset)
//                        ?: return@intercept

                proceedWith(container.copy(response = body))
            }

            return PlainText()
        }
    }
}