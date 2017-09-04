package http.features

import http.pipeline.HttpClientScope
import http.request.HttpRequestDataBuilder
import http.request.HttpRequestPipeline
import http.response.HttpResponseData
import http.response.HttpResponsePipeline
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

        fun build(): Cookies {
            return Cookies(storage)
        }
    }

    companion object Feature : HttpClientScopeFeature<Configuration, Cookies> {
        override val key: AttributeKey<Cookies> = AttributeKey("Cookies")

        override fun install(scope: HttpClientScope, configure: Configuration.() -> Unit): Cookies {
            val cookies = Configuration().apply(configure).build()

            scope.requestPipeline.intercept(HttpRequestPipeline.State) { data ->
                val request = (data as? HttpRequestDataBuilder) ?: return@intercept

                request.headers {
                    cookies.storage.forEach(request.url.host) {
                        set(HttpHeaders.Cookie, renderSetCookieHeader(it))
                    }
                }
            }

            scope.responsePipeline.intercept(HttpResponsePipeline.Transform) { data ->
                val response = data.response as? HttpResponseData ?: return@intercept

                response.headers.getAll(HttpHeaders.SetCookie)?.map { parseServerSetCookieHeader(it) }?.forEach {
                    cookies.storage[data.request.url.host] = it
                }
            }

            return cookies
        }
    }
}

fun HttpClientScope.cookies(host: String): Map<String, Cookie> = feature(Cookies)[host] ?: mapOf()

