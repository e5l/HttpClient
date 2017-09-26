package http.features

import http.pipeline.ClientScope
import org.jetbrains.ktor.util.AttributeKey
import org.jetbrains.ktor.util.Attributes

internal val FEATURE_INSTALLED_LIST = AttributeKey<Attributes>("ApplicationFeatureRegistry")

interface ClientFeature<out TBuilder : Any, TFeature : Any> {
    val key: AttributeKey<TFeature>

    fun prepare(configure: TBuilder.() -> Unit): TFeature

    fun install(feature: TFeature, scope: ClientScope)
}

fun <B : Any, F : Any> ClientScope.feature(feature: ClientFeature<B, F>): F? =
        attributes.getOrNull(FEATURE_INSTALLED_LIST)?.getOrNull(feature.key)
