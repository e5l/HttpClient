package http.features

import http.HttpClientScope
import org.jetbrains.ktor.util.AttributeKey

interface HttpClientScopeFeature<out TBuilder : Any, TFeature : Any> {
    val key: AttributeKey<TFeature>

    fun install(scope: HttpClientScope, configure: TBuilder.() -> Unit): TFeature
}

fun <D: HttpClientScope, TBuilder: Any, TFeature: Any> D.install(
        feature: HttpClientScopeFeature<TBuilder, TFeature>,
        block: TBuilder.() -> Unit = {}
) {
    feature.install(this, block)
}