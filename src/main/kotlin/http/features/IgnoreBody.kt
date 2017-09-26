package http.features

import http.pipeline.ClientScope
import http.response.ResponsePipeline
import org.jetbrains.ktor.util.AttributeKey

class IgnoreBody {
    companion object Feature : ClientFeature<Unit, IgnoreBody> {

        override fun prepare(configure: Unit.() -> Unit): IgnoreBody = IgnoreBody()

        override val key: AttributeKey<IgnoreBody> = AttributeKey("IgnoreBody")

        override fun install(feature: IgnoreBody, scope: ClientScope) {
            scope.responsePipeline.intercept(ResponsePipeline.Transform) { data ->
                if (data.expectedType != Unit::class) {
                    return@intercept
                }

                data.response.payload = Unit
            }
        }

    }
}