package http.features

import http.call.HttpClientCall
import http.pipeline.HttpClientScope
import http.request.HttpRequest
import http.request.HttpRequestBuilder
import http.request.HttpRequestPipeline
import http.response.HttpResponse
import http.response.HttpResponseContainer
import http.response.HttpResponsePipeline
import http.utils.Headers
import http.utils.HeadersBuilder
import http.utils.Url
import http.utils.vary
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.util.AttributeKey
import java.util.*

fun Iterable<Boolean>.all(): Boolean = all { it }

private data class CacheEntity(
        val invariant: Map<String, Set<String>>,
        val cache: HttpResponse
) {
    fun match(headers: HeadersBuilder): Boolean = invariant.entries.mapNotNull { (name, values) ->
        headers.getAll(name)?.toSet()?.let { it == values }
    }.all()
}

class HttpCache {
    private val responseCache = mutableMapOf<Url, CacheEntity>()

    private fun cacheResponse(responseContainer: HttpResponseContainer) {
        val (_, request, response) = responseContainer
        if (response.statusCode != HttpStatusCode.OK && response.statusCode != HttpStatusCode.NotModified) return

        with(request.cacheControl) {
            if (noCache || noStore) return
        }

        with(response.cacheControl) {
            if (noCache || noStore) return
        }

        response.headers.vary()?.let { vary ->
            cacheWithVary(request.url, vary, request.headers, response.build())
        }
    }

    private fun tryLoad(builder: HttpRequestBuilder): HttpResponse? {
        val result = load(builder)
        if (result == null && builder.cacheControl.onlyIfCached) {
            throw NotFoundInCacheException(builder.build())
        }

        return result
    }

    private fun load(builder: HttpRequestBuilder): HttpResponse? {
        val now = Date()
        val url = builder.url.build()
        val cachedResponse = responseCache[url]?.takeIf { it.match(builder.headers) }?.cache ?: return null

        return if (isValid(builder, cachedResponse)) cachedResponse else null
    }

    private fun isValid(builder: HttpRequestBuilder, response: HttpResponse): Boolean {
        val now = Date().time
        val requestTime = response.requestTime.time

        with(response.cacheControl) {
            maxAge?.let { if (requestTime + it > now) return false }
        }

        with(builder.cacheControl) {
            maxAge?.let { if (requestTime + it > now) return false }
        }

        return true
    }

    private fun cacheWithVary(url: Url, varyHeaders: List<String>, requestHeaders: Headers, response: HttpResponse) {
        val invariant = varyHeaders.map { key ->
            key to (requestHeaders.getAll(key)?.toSet() ?: setOf())
        }.toMap()

        responseCache[url] = CacheEntity(invariant, response)
    }

    companion object Feature : HttpClientFeature<Unit, HttpCache> {
        override val key: AttributeKey<HttpCache> = AttributeKey("HttpCache")

        override fun prepare(block: Unit.() -> Unit): HttpCache = HttpCache()

        override fun install(feature: HttpCache, scope: HttpClientScope) {
            scope.requestPipeline.intercept(HttpRequestPipeline.Send) { builder ->
                if (builder !is HttpRequestBuilder) return@intercept

                feature.tryLoad(builder)?.let {
                    proceedWith(HttpClientCall(builder.build(), it, scope))
                }
            }

            scope.responsePipeline.intercept(HttpResponsePipeline.After) { responseData ->
                feature.cacheResponse(responseData)
            }
        }
    }
}

class NotFoundInCacheException(val request: HttpRequest) : Exception()