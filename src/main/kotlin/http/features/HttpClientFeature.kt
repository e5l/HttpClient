package http.features

import http.pipeline.HttpClientScope
import org.jetbrains.ktor.util.AttributeKey
import org.jetbrains.ktor.util.Attributes

internal val FEATURE_INSTALLED_LIST = AttributeKey<Attributes>("ApplicationFeatureRegistry")

interface HttpClientFeature<out TBuilder : Any, TFeature : Any> {
    val key: AttributeKey<TFeature>

    fun prepare(block: TBuilder.() -> Unit): TFeature

    fun install(feature: TFeature, scope: HttpClientScope)
}

fun <B : Any, F : Any> HttpClientScope.feature(feature: HttpClientFeature<B, F>): F? =
        attributes.getOrNull(FEATURE_INSTALLED_LIST)?.getOrNull(feature.key)
