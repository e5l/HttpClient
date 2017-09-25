package http.features

import http.pipeline.ClientScope
import http.pipeline.EmptyScope
import org.jetbrains.ktor.util.AttributeKey
import org.jetbrains.ktor.util.Attributes

private val featureRegistryKey = AttributeKey<Attributes>("ApplicationFeatureRegistry")

interface ClientScopeFeature<out TBuilder : Any, TFeature : Any> {
    val key: AttributeKey<TFeature>

    fun install(scope: ClientScope, configure: TBuilder.() -> Unit): TFeature
}

// TODO: refactor with common part in ktor
fun <TBuilder : Any, TFeature : Any> ClientScope.install(
        feature: ClientScopeFeature<TBuilder, TFeature>,
        configure: TBuilder.() -> Unit = {}
): TFeature {
    val registry = attributes.computeIfAbsent(featureRegistryKey) { Attributes() }
    val installedFeature = registry.getOrNull(feature.key)
    when (installedFeature) {
        null -> {
            try {
                @Suppress("DEPRECATION_ERROR")
                val installed = feature.install(this, configure)
                registry.put(feature.key, installed)
                //environment.log.trace("`${feature.name}` feature was installed successfully.")
                return installed
            } catch (t: Throwable) {
                //environment.log.error("`${feature.name}` feature failed to install.", t)
                throw t
            }
        }
        feature -> {
            //environment.log.warning("`${feature.name}` feature is already installed")
            return installedFeature
        }
        else -> {
            throw DuplicateApplicationFeatureException("Conflicting application feature is already installed with the same key as `${feature.key.name}`")
        }
    }
}

fun <B : Any, F : Any> ClientScope.feature(feature: ClientScopeFeature<B, F>): F {
    var it = this
    while (it !is EmptyScope) {
        it.attributes.getOrNull(featureRegistryKey)?.getOrNull(feature.key)?.let { return it }
        it = it.parent
    }

    error("Feature not found: ${feature.key}")
}

class DuplicateApplicationFeatureException(message: String) : Exception(message)
