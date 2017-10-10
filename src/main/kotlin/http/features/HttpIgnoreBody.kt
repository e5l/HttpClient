package http.features

import http.pipeline.HttpClientScope
import http.response.HttpResponsePipeline
import org.jetbrains.ktor.util.AttributeKey

class HttpIgnoreBody {
    companion object Feature : HttpClientFeature<Unit, HttpIgnoreBody> {

        override fun prepare(block: Unit.() -> Unit): HttpIgnoreBody = HttpIgnoreBody()

        override val key: AttributeKey<HttpIgnoreBody> = AttributeKey("HttpIgnoreBody")

        override fun install(feature: HttpIgnoreBody, scope: HttpClientScope) {
            scope.responsePipeline.intercept(HttpResponsePipeline.Transform) { data ->
                if (data.expectedType != Unit::class) {
                    return@intercept
                }

                data.response.payload = Unit
            }
        }

    }
}