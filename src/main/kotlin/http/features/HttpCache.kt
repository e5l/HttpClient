package http.features

import http.call.HttpClientCall
import http.call.call
import http.pipeline.HttpClientScope
import http.request.HttpRequest
import http.request.HttpRequestBuilder
import http.request.HttpRequestPipeline
import http.response.HttpResponse
import http.response.HttpResponseBuilder
import http.response.HttpResponsePipeline
import http.utils.*
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.util.AttributeKey
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

    private fun load(builder: HttpRequestBuilder): HttpResponse? {
        val now = Date()
        val url = builder.url.build()
        return responseCache[url]?.takeIf { it.match(builder.headers) }?.cache
    }

    private fun isValid(response: HttpResponse, builder: HttpRequestBuilder): Boolean {
        val now = Date().time
        val requestTime = response.requestTime.time

        with(response.cacheControl) {
            maxAge?.let { if (requestTime + it > now) return false }
            if (mustRevalidate) return false
        }

        with(builder.cacheControl) {
            maxAge?.let { if (requestTime + it > now) return false }
        }

        return true
    }

    private suspend fun validate(
            cachedResponse: HttpResponse,
            builder: HttpRequestBuilder,
            scope: HttpClientScope
    ): HttpClientCall? {
        val request = HttpRequestBuilder(builder.build())

        val lastModified = cachedResponse.lastModified()
        val etag = cachedResponse.etag()

        if (lastModified == null && etag == null) return null

        etag?.let { request.ifMatch(it) }
        lastModified?.let { request.ifModifiedSince(it) }

        val call = scope.call(request)
        return when (call.response.statusCode) {
            HttpStatusCode.NotModified -> HttpClientCall(builder.build(), cachedResponse, scope)
            HttpStatusCode.OK -> {
                cacheResponse(call.request, HttpResponseBuilder(call.response))
                call
            }
            else -> null
        }
    }

    private fun cacheResponse(request: HttpRequest, response: HttpResponseBuilder) {
        if (response.statusCode != HttpStatusCode.OK) return

        with(request.cacheControl) {
            if (noCache || noStore) return
        }

        with(response.cacheControl) {
            if (noCache || noStore) return
        }

        cache(request.url, request.headers, response.build())
    }

    private fun cache(url: Url, requestHeaders: Headers, response: HttpResponse) {
        val varyHeaders = response.vary() ?: listOf()

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
                if (builder.method != HttpMethod.Get) return@intercept

                val cache = feature.load(builder) ?: return@intercept
                if (feature.isValid(cache, builder)) {
                    proceedWith(HttpClientCall(builder.build(), cache, scope))
                    return@intercept
                }

                feature.validate(cache, builder, scope)?.let { proceedWith(it) }
            }

            scope.responsePipeline.intercept(HttpResponsePipeline.After) { (_, request, response) ->
                if (request.method != HttpMethod.Get) return@intercept
                feature.cacheResponse(request, response)
            }
        }
    }
}

class NotFoundInCacheException(val request: HttpRequest) : Exception()