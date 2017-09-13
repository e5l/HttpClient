package http.features

import http.pipeline.ClientScope
import http.request.RequestPipeline
import http.response.ResponsePipeline
import org.jetbrains.ktor.http.Cookie
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.http.parseServerSetCookieHeader
import org.jetbrains.ktor.http.renderSetCookieHeader
import org.jetbrains.ktor.util.AttributeKey

class CookiesStorage {
    private val data = mutableMapOf<String, MutableMap<String, Cookie>>()

    operator fun set(host: String, cookie: Cookie) {
        init(host)
        data[host]?.set(cookie.name, cookie)
    }

    operator fun get(host: String): Map<String, Cookie>? = data[host]

    fun forEach(host: String, block: (Cookie) -> Unit) {
        init(host)
        data[host]?.values?.forEach(block)
    }

    private fun init(host: String) {
        if (!data.containsKey(host)) {
            data[host] = mutableMapOf()
        }
    }
}

class Cookies(private val storage: CookiesStorage) {

    operator fun get(host: String): Map<String, Cookie>? = storage[host]

    class Configuration {
        var storage = CookiesStorage()

        operator fun set(host: String, cookie: Cookie) {
            storage[host] = cookie
        }

        fun build(): Cookies = Cookies(storage)
    }

    companion object Feature : ClientScopeFeature<Configuration, Cookies> {
        override val key: AttributeKey<Cookies> = AttributeKey("Cookies")

        override fun install(scope: ClientScope, configure: Configuration.() -> Unit): Cookies {
            val cookies = Configuration().apply(configure).build()

            scope.requestPipeline.intercept(RequestPipeline.State) {
                cookies.storage[call.requestBuilder.url.host]?.values?.forEach {
                    call.requestBuilder.headers.append(HttpHeaders.Cookie, renderSetCookieHeader(it))
                }
            }

            scope.responsePipeline.intercept(ResponsePipeline.Transform) {
                val headers = call.response.data.headers
                headers.getAll(HttpHeaders.SetCookie)?.map { parseServerSetCookieHeader(it) }?.forEach {
                    cookies.storage[call.request.data.local.host] = it
                }
            }

            return cookies
        }
    }
}

fun ClientScope.cookies(host: String): Map<String, Cookie> = feature(Cookies)[host] ?: mapOf()

