package http.pipeline

import http.features.ClientFeature
import http.features.FEATURE_INSTALLED_LIST
import http.features.IgnoreBody
import http.features.PlainText
import org.jetbrains.ktor.util.AttributeKey
import org.jetbrains.ktor.util.Attributes

private val CLIENT_CONFIG_KEY = AttributeKey<ClientConfig>("ClientConfig")

class ClientConfig {
    private val features = mutableMapOf<AttributeKey<*>, (ClientScope) -> Unit>()
    private val customInterceptors = mutableMapOf<String, (ClientScope) -> Unit>()

    fun <TBuilder : Any, TFeature : Any> install(
            feature: ClientFeature<TBuilder, TFeature>,
            configure: TBuilder.() -> Unit = {}
    ) {
        val featureData = feature.prepare(configure)

        features[feature.key] = { scope ->
            val attributes = scope.attributes.computeIfAbsent(FEATURE_INSTALLED_LIST) { Attributes() }

            feature.install(featureData, scope)
            attributes.put(feature.key, featureData)
        }
    }

    fun install(key: String, block: ClientScope.() -> Unit) {
        customInterceptors[key] = block
    }

    fun build(): ClientScope {
        val scope = CallScope()
        scope.attributes.put(CLIENT_CONFIG_KEY, this)

        features.values.forEach { scope.apply(it) }
        customInterceptors.values.forEach { scope.apply(it) }

        return scope
    }

    fun clone(): ClientConfig {
        val result = ClientConfig()
        result.features.putAll(features)
        result.customInterceptors.putAll(customInterceptors)

        return result
    }
}

fun ClientScope.config(block: ClientConfig.() -> Unit): ClientScope {
    val config = attributes.computeIfAbsent(CLIENT_CONFIG_KEY) { ClientConfig() }
    return config.clone().apply(block).build()
}

fun ClientScope.default(features: List<ClientFeature<Any, out Any>> = listOf(PlainText, IgnoreBody)) = config {
    features.forEach { install(it) }
}
