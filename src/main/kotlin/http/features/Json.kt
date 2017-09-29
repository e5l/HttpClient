package http.features

import http.pipeline.ClientScope
import http.request.RequestBuilder
import http.request.RequestPipeline
import http.request.accept
import http.response.ResponsePipeline
import http.response.contentType
import http.utils.safeAs
import kotlinx.serialization.json.JSON
import kotlinx.serialization.serializer
import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.util.AttributeKey

class Json {
    companion object Feature : ClientFeature<Unit, Json> {
        override val key: AttributeKey<Json> = AttributeKey("json")

        override fun prepare(configure: Unit.() -> Unit): Json = Json()

        override fun install(feature: Json, scope: ClientScope) {
            scope.requestPipeline.intercept(RequestPipeline.Transform) { request ->
                val requestBuilder = request.safeAs<RequestBuilder>() ?: return@intercept
                requestBuilder.accept(ContentType.Application.Json)
            }

            scope.responsePipeline.intercept(ResponsePipeline.Transform) { (expectedType, _, response) ->
                if (response.contentType()?.match(ContentType.Application.Json) != true) return@intercept
                val reader = scope.feature(PlainText) ?: return@intercept

                val serializer = expectedType.serializer()
                val content = reader.read(response) ?: return@intercept

                response.payload = JSON.parse(serializer, content)
            }
        }
    }
}
