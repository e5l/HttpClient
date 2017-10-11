package http.pipeline

import http.features.HttpClientFeature
import http.features.FEATURE_INSTALLED_LIST
import http.features.HttpIgnoreBody
import http.features.HttpPlainText
import io.ktor.util.AttributeKey
import io.ktor.util.Attributes

private val CLIENT_CONFIG_KEY = AttributeKey<ClientConfig>("ClientConfig")

class ClientConfig {
    private val features = mutableMapOf<AttributeKey<*>, (HttpClientScope) -> Unit>()
    private val customInterceptors = mutableMapOf<String, (HttpClientScope) -> Unit>()

    fun <TBuilder : Any, TFeature : Any> install(
            feature: HttpClientFeature<TBuilder, TFeature>,
            configure: TBuilder.() -> Unit = {}
    ) {
        val featureData = feature.prepare(configure)

        features[feature.key] = { scope ->
            val attributes = scope.attributes.computeIfAbsent(FEATURE_INSTALLED_LIST) { Attributes() }

            feature.install(featureData, scope)
            attributes.put(feature.key, featureData)
        }
    }

    fun install(key: String, block: HttpClientScope.() -> Unit) {
        customInterceptors[key] = block
    }

    fun build(): HttpClientScope {
        val scope = HttpCallScope()
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

fun HttpClientScope.config(block: ClientConfig.() -> Unit): HttpClientScope {
    val config = attributes.computeIfAbsent(CLIENT_CONFIG_KEY) { ClientConfig() }
    return config.clone().apply(block).build()
}

fun HttpClientScope.default(features: List<HttpClientFeature<Any, out Any>> = listOf(HttpPlainText, HttpIgnoreBody)) = config {
    features.forEach { install(it) }
}
