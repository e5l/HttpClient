package http.features

import http.bodyText
import http.pipeline.HttpClientScope
import http.response.HttpResponseData
import http.response.HttpResponsePipeline
import org.jetbrains.ktor.util.AttributeKey

inline fun <reified T> Any?.safeAs(): T? = this as? T

class PlainText {
    class Configuration

    companion object Feature : HttpClientScopeFeature<Configuration, PlainText> {
        override val key = AttributeKey<PlainText>("PlainText")

        override fun install(scope: HttpClientScope, configure: Configuration.() -> Unit): PlainText {
            scope.responsePipeline.intercept(HttpResponsePipeline.Transform) { container ->
                if (container.expectedType != String::class) {
                    return@intercept
                }

                val body = container.response.safeAs<HttpResponseData>()?.bodyText() ?: return@intercept
                proceedWith(container.copy(response = body))
            }

            return PlainText()
        }
    }
}