package http.features.cookies

import http.features.ClientScopeFeature
import http.features.feature
import http.pipeline.ClientScope
import http.request.RequestPipeline
import http.response.ResponsePipeline
import org.jetbrains.ktor.http.*
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

    companion object Feature : ClientScopeFeature<Configuration, Cookies> {
        override val key: AttributeKey<Cookies> = AttributeKey("Cookies")

        override fun install(scope: ClientScope, configure: Configuration.() -> Unit): Cookies {
            val cookies = Configuration().apply(configure).build()

            scope.requestPipeline.intercept(RequestPipeline.State) {
                val host = call.requestBuilder.url.host
                cookies.forEach(host) {
                    call.requestBuilder.headers.append(HttpHeaders.Cookie, renderSetCookieHeader(it))
                }
            }

            scope.responsePipeline.intercept(ResponsePipeline.Transform) {
                val headers = call.response.data.headers
                headers.getAll(HttpHeaders.SetCookie)?.map { parseServerSetCookieHeader(it) }?.forEach {
                    cookies.storage[call.requestBuilder.url.host] = it
                }
            }

            return cookies
        }
    }
}

fun ClientScope.cookies(host: String): Map<String, Cookie> = feature(Cookies)[host] ?: mapOf()

