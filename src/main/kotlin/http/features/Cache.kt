package http.features

import http.call.HttpClientCall
import http.pipeline.ClientScope
import http.request.Request
import http.request.RequestBuilder
import http.request.RequestPipeline
import http.response.Response
import http.response.ResponseContainer
import http.response.ResponsePipeline
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
        val cache: Response
) {
    fun match(headers: HeadersBuilder): Boolean = invariant.entries.mapNotNull { (name, values) ->
        headers.getAll(name)?.toSet()?.let { it == values }
    }.all()
}

class Cache {
    private val responseCache = mutableMapOf<Url, CacheEntity>()

    private fun cacheResponse(responseContainer: ResponseContainer) {
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

    private fun tryLoad(builder: RequestBuilder): Response? {
        val result = load(builder)
        if (result == null && builder.cacheControl.onlyIfCached) {
            throw NotFoundInCacheException(builder.build())
        }

        return result
    }

    private fun load(builder: RequestBuilder): Response? {
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

    private fun cacheWithVary(url: Url, varyHeaders: List<String>, requestHeaders: Headers, response: Response) {
        val invariant = varyHeaders.map { key ->
            key to (requestHeaders.getAll(key)?.toSet() ?: setOf())
        }.toMap()

        responseCache[url] = CacheEntity(invariant, response)
    }

    companion object Feature : ClientFeature<Unit, Cache> {
        override val key: AttributeKey<Cache> = AttributeKey("Cache")

        override fun prepare(block: Unit.() -> Unit): Cache = Cache()

        override fun install(feature: Cache, scope: ClientScope) {
            scope.requestPipeline.intercept(RequestPipeline.Send) { builder ->
                if (builder !is RequestBuilder) return@intercept

                feature.tryLoad(builder)?.let {
                    proceedWith(HttpClientCall(builder.build(), it, scope))
                }
            }

            scope.responsePipeline.intercept(ResponsePipeline.After) { responseData ->
                feature.cacheResponse(responseData)
            }
        }
    }
}

class NotFoundInCacheException(val request: Request) : Exception()