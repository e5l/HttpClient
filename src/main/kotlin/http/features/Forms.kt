package http.features

import http.pipeline.ClientScope
import http.request.RequestPipeline
import http.utils.safeAs
import kotlinx.serialization.Mapper
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.util.AttributeKey

enum class FormType {
    URL_ENCODED,
    MULTIPART
}

data class FormData(val data: Any, val type: FormType = FormType.URL_ENCODED, val method: HttpMethod = HttpMethod.Get)

class Forms {
    object Feature : ClientFeature<Unit, Forms> {
        override fun prepare(configure: Unit.() -> Unit): Forms {
            return Forms()
        }

        override val key: AttributeKey<Forms> = AttributeKey("Forms")

        override fun install(feature: Forms, scope: ClientScope) {
            scope.requestPipeline.intercept(RequestPipeline.Content) { subject ->
                val form = subject.safeAs<FormData>() ?: return@intercept

                when (form.type) {
                    FormType.URL_ENCODED -> {
                        val parameters = Mapper.map(form.data)
                        when (form.method) {
                            HttpMethod.Get -> TODO("Forms")
                            HttpMethod.Post -> TODO("Forms")
                            else -> TODO("Forms")
                        }
                    }
                    FormType.MULTIPART -> TODO("Forms")
                }
            }
        }
    }
}
