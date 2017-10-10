package http.features.cookies

import http.features.HttpClientFeature
import http.features.feature
import http.pipeline.HttpClientScope
import http.request.HttpRequestBuilder
import http.request.HttpRequestPipeline
import http.request.header
import http.request.host
import http.response.HttpResponsePipeline
import http.response.cookies
import http.utils.safeAs
import org.jetbrains.ktor.http.Cookie
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.http.renderSetCookieHeader
import org.jetbrains.ktor.util.AttributeKey

class HttpCookies(private val storage: CookiesStorage) {

    operator fun get(host: String): Map<String, Cookie>? = storage[host]

    operator fun get(host: String, name: String): Cookie? = storage[host, name]

    fun forEach(host: String, block: (Cookie) -> Unit) = storage.forEach(host, block)

    class Configuration {
        private val defaults = mutableListOf<CookiesStorage.() -> Unit>()

        var storage: CookiesStorage = AcceptAllCookiesStorage()

        fun default(block: CookiesStorage.() -> Unit) {
            defaults.add(block)
        }

        fun build(): HttpCookies {
            defaults.forEach { storage.apply(it) }
            return HttpCookies(storage)
        }
    }

    companion object Feature : HttpClientFeature<Configuration, HttpCookies> {
        override fun prepare(block: Configuration.() -> Unit): HttpCookies = Configuration().apply(block).build()

        override val key: AttributeKey<HttpCookies> = AttributeKey("HttpCookies")

        override fun install(feature: HttpCookies, scope: HttpClientScope) {

            scope.requestPipeline.intercept(HttpRequestPipeline.State) { requestData ->
                val request = requestData.safeAs<HttpRequestBuilder>() ?: return@intercept
                feature.forEach(request.host) {
                    request.header(HttpHeaders.Cookie, renderSetCookieHeader(it))
                }
            }

            scope.responsePipeline.intercept(HttpResponsePipeline.State) { (_, request, response) ->
                response.cookies().forEach {
                    feature.storage[request.host] = it
                }
            }
        }
    }
}

fun HttpClientScope.cookies(host: String): Map<String, Cookie> = feature(HttpCookies)?.get(host) ?: mapOf()
