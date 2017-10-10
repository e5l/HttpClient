package http.features

import http.pipeline.HttpClientScope
import http.pipeline.intercept
import http.request.HttpRequestBuilder
import http.request.HttpRequestPipeline
import http.utils.safeAs
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Mapper
import kotlinx.serialization.serializer
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.http.formUrlEncode
import org.jetbrains.ktor.util.AttributeKey

enum class FormType {
    URL_ENCODED,
    MULTIPART
}

data class FormData(val data: Any, val type: FormType = FormType.URL_ENCODED, val method: HttpMethod = HttpMethod.Get)

class HttpForms {
    companion object Feature : HttpClientFeature<Unit, HttpForms> {
        override fun prepare(block: Unit.() -> Unit): HttpForms {
            return HttpForms()
        }

        override val key: AttributeKey<HttpForms> = AttributeKey("HttpForms")

        override fun install(feature: HttpForms, scope: HttpClientScope) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Transform) { builder: HttpRequestBuilder ->
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
                    FormType.MULTIPART -> TODO("HttpForms")
                }
            }
        }
    }
}

private fun serializeData(data: Any): List<Pair<String, String>> {
    val mapper = Mapper.OutMapper()
    val serializer = data::class.serializer() as KSerializer<Any>
    mapper.write(serializer, data)
    return mapper.map.map { (key, value) -> key to value.toString() }
}
