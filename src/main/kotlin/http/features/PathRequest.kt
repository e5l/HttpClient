package http.features

import http.HttpClientScope
import http.backend.HttpRequestData
import http.request.HttpRequestPipeline
import org.jetbrains.ktor.http.HttpMethod
import org.jetbrains.ktor.util.AttributeKey

class PathRequest(val path: String = "", val method: HttpMethod = HttpMethod.Get) {

    class Configuration

    companion object Feature : HttpClientScopeFeature<Configuration, Unit> {
        override val key: AttributeKey<Unit> = AttributeKey("PathRequest")

        override fun install(scope: HttpClientScope, configure: Configuration.() -> Unit) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Route) { request ->
                if (request !is PathRequest) {
                    return@intercept
                }

                val builder = HttpRequestData.Builder().apply {
                    method = request.method
                    path = request.path
                }

                proceedWith(builder)
            }
        }
    }
}
