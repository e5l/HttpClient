package http.features

import http.pipeline.HttpClientScope
import org.jetbrains.ktor.util.AttributeKey

class Cookies {
    class Configration

    companion object Feature : HttpClientScopeFeature<Configration, Cookies> {
        override val key: AttributeKey<Cookies>
            get() = TODO()

        override fun install(scope: HttpClientScope, configure: Configration.() -> Unit): Cookies {
            TODO()
        }

    }
}
