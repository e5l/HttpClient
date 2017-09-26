package http.features

import http.pipeline.ClientScope
import http.response.ResponsePipeline
import org.jetbrains.ktor.util.AttributeKey

class IgnoreBody {
    companion object Feature : ClientScopeFeature<Unit, IgnoreBody> {
        override val key: AttributeKey<IgnoreBody> = AttributeKey("IgnoreBody")

        override fun install(scope: ClientScope, configure: Unit.() -> Unit): IgnoreBody {
            scope.responsePipeline.intercept(ResponsePipeline.Transform) { data ->
                if (data.expectedType != Unit::class) {
                    return@intercept
                }

                data.response.payload = Unit
            }

            return IgnoreBody()
        }

    }
}