package http.features

import http.pipeline.HttpClientScope
import http.response.HttpResponseData
import http.response.HttpResponsePipeline
import http.response.ResponseContainer
import org.jetbrains.ktor.util.AttributeKey

class PlainText {
    class Configuration

    companion object Feature : HttpClientScopeFeature<Configuration, PlainText> {
        override val key = AttributeKey<PlainText>("Transformer")

        override fun install(scope: HttpClientScope, configure: Configuration.() -> Unit): PlainText {
            scope.responsePipeline.intercept(HttpResponsePipeline.Transform) { (expectedType, value) ->
                if (value !is HttpResponseData) {
                    return@intercept
                }

                if (expectedType != String::class) {
                    return@intercept
                }

                proceedWith(ResponseContainer(expectedType, value.body))
            }

            return PlainText()
        }
    }
}