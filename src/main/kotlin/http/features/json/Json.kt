package http.features.json

import http.features.HttpClientFeature
import http.features.HttpPlainText
import http.features.feature
import http.pipeline.HttpClientScope
import http.request.HttpRequestBuilder
import http.request.HttpRequestPipeline
import http.request.accept
import http.response.HttpResponsePipeline
import http.response.contentType
import http.utils.safeAs
import io.ktor.http.ContentType
import io.ktor.util.AttributeKey

class Json(val serializer: JsonSerializer) {

    class Config {
        var serializer: JsonSerializer = DefaultSerializer()
    }

    companion object Feature : HttpClientFeature<Config, Json> {
        override val key: AttributeKey<Json> = AttributeKey("json")

        override fun prepare(block: Config.() -> Unit): Json = Config().apply(block).let { Json(it.serializer) }

        override fun install(feature: Json, scope: HttpClientScope) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Transform) { request ->
                val requestBuilder = request.safeAs<HttpRequestBuilder>() ?: return@intercept
                requestBuilder.accept(ContentType.Application.Json)
            }

            scope.responsePipeline.intercept(HttpResponsePipeline.Transform) { (expectedType, _, response) ->
                if (response.contentType()?.match(ContentType.Application.Json) != true) return@intercept
                val reader = scope.feature(HttpPlainText) ?: return@intercept
                val content = reader.read(response) ?: return@intercept

                response.payload = feature.serializer.read(expectedType, content)
            }
        }
    }
}
