package http.features

import http.pipeline.ClientScope
import http.request.RequestBuilder
import http.request.RequestPipeline
import http.utils.safeAs
import kotlinx.serialization.Mapper
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.http.formUrlEncode
import org.jetbrains.ktor.util.AttributeKey

enum class FormType {
    URL_ENCODED,
    MULTIPART
}

data class FormData(val data: Any, val type: FormType = FormType.URL_ENCODED, val method: HttpMethod = HttpMethod.Get)

class Forms {

    companion object Feature : ClientFeature<Unit, Forms> {
        override fun prepare(configure: Unit.() -> Unit): Forms {
            return Forms()
        }

        override val key: AttributeKey<Forms> = AttributeKey("Forms")

        override fun install(feature: Forms, scope: ClientScope) {
            scope.requestPipeline.intercept(RequestPipeline.Content) { requestBuilder ->
                val builder = requestBuilder.safeAs<RequestBuilder>() ?: return@intercept
                val form = builder.payload.safeAs<FormData>() ?: return@intercept

                when (form.type) {
                    FormType.URL_ENCODED -> {
                        val parameters = serializeData(form.data)
                        when (form.method) {
                            HttpMethod.Get -> builder.url {
                                parameters.forEach { (key, value) -> queryParameters.append(key, value) }
                            }
                            else -> builder.payload = parameters.formUrlEncode()
                        }
                    }
                    FormType.MULTIPART -> TODO("Forms")
                }
            }
        }
    }
}

private fun serializeData(data: Any): List<Pair<String, String>> =
        Mapper.map(data).map { (key, value) -> key to value.toString() }
