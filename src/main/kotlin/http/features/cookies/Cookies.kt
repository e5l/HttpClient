package http.features.cookies

import http.features.ClientFeature
import http.features.feature
import http.pipeline.ClientScope
import http.request.RequestBuilder
import http.request.RequestPipeline
import http.request.header
import http.request.host
import http.response.ResponsePipeline
import http.response.cookies
import http.utils.safeAs
import org.jetbrains.ktor.http.Cookie
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.http.renderSetCookieHeader
import org.jetbrains.ktor.util.AttributeKey

class Cookies(private val storage: CookiesStorage) {

    operator fun get(host: String): Map<String, Cookie>? = storage[host]

    operator fun get(host: String, name: String): Cookie? = storage[host, name]

    fun forEach(host: String, block: (Cookie) -> Unit) = storage.forEach(host, block)

    class Configuration {
        private val defaults = mutableListOf<CookiesStorage.() -> Unit>()

        var storage: CookiesStorage = AcceptAllCookiesStorage()

        fun default(block: CookiesStorage.() -> Unit) {
            defaults.add(block)
        }

        fun build(): Cookies {
            defaults.forEach { storage.apply(it) }
            return Cookies(storage)
        }
    }

    companion object Feature : ClientFeature<Configuration, Cookies> {
        override fun prepare(configure: Configuration.() -> Unit): Cookies = Configuration().apply(configure).build()

        override val key: AttributeKey<Cookies> = AttributeKey("Cookies")

        override fun install(feature: Cookies, scope: ClientScope) {

            scope.requestPipeline.intercept(RequestPipeline.State) { requestData ->
                val request = requestData.safeAs<RequestBuilder>() ?: return@intercept
                val host = request.url.host
                feature.forEach(request.host) {
                    request.header(HttpHeaders.Cookie, renderSetCookieHeader(it))
                }
            }

            scope.responsePipeline.intercept(ResponsePipeline.State) { (_, request, response) ->
                response.cookies().forEach {
                    feature.storage[request.host] = it
                }
            }
        }
    }
}

fun ClientScope.cookies(host: String): Map<String, Cookie> = feature(Cookies)?.get(host) ?: mapOf()
