package http.features

import http.HttpClientScope
import http.backend.HttpRequestData
import http.backend.HttpScheme
import http.request.HttpRequestPipeline
import org.jetbrains.ktor.util.AttributeKey

object Https : HttpClientScopeFeature<Https.Configuration, Https> {
    override val key: AttributeKey<Https> = AttributeKey("Method")

    override fun install(scope: HttpClientScope, configure: Configuration.() -> Unit): Https {
        scope.requestPipeline.intercept(HttpRequestPipeline.Address) { request ->
            if (request is HttpRequestData.Builder) {
                request.scheme = HttpScheme.Https
                request.port = 443
            }
        }

        return Https
    }

    class Configuration
}