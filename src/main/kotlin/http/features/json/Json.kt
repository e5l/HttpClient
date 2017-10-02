package http.features.json

import http.features.ClientFeature
import http.features.PlainText
import http.features.feature
import http.pipeline.ClientScope
import http.request.RequestBuilder
import http.request.RequestPipeline
import http.request.accept
import http.response.ResponsePipeline
import http.response.contentType
import http.utils.safeAs
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.util.AttributeKey

class Json(val serializer: JsonSerializer) {

    class Config {
        var serializer: JsonSerializer = DefaultSerializer()
    }

    companion object Feature : ClientFeature<Config, Json> {
        override val key: AttributeKey<Json> = AttributeKey("json")

        override fun prepare(block: Config.() -> Unit): Json = Config().apply(block).let { Json(it.serializer) }

        override fun install(feature: Json, scope: ClientScope) {
            scope.requestPipeline.intercept(RequestPipeline.Transform) { request ->
                val requestBuilder = request.safeAs<RequestBuilder>() ?: return@intercept
                requestBuilder.accept(ContentType.Application.Json)
            }

            scope.responsePipeline.intercept(ResponsePipeline.Transform) { (expectedType, _, response) ->
                if (response.contentType()?.match(ContentType.Application.Json) != true) return@intercept
                val reader = scope.feature(PlainText) ?: return@intercept
                val content = reader.read(response) ?: return@intercept

                response.payload = feature.serializer.read(expectedType, content)
            }
        }
    }
}
