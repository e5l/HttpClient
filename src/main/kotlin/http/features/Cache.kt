package http.features

import http.call.HttpClientCall
import http.pipeline.ClientScope
import http.request.RequestBuilder
import http.request.RequestPipeline
import http.response.Response
import http.response.ResponseContainer
import http.response.ResponsePipeline
import http.utils.*
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.util.AttributeKey
import java.util.*

fun Iterable<Boolean>.all(): Boolean = all { it }

private data class CacheEntity(
        val invariant: Map<String, Set<String>>,
        val cache: Response
) {
    val received: Date = cache.headers.date() ?: Date()

    fun match(headers: HeadersBuilder): Boolean = invariant.entries.mapNotNull { (name, values) ->
        headers.getAll(name)?.toSet()?.let { it == values }
    }.all()

    fun expired(): Boolean {
        val now = Date()

        cache.headers.maxAge()?.let {
            // TODO: fix time difference
            return now.time - received.time < it * 1000 }

        if (now.after(cache.headers.expires())) {
            return true
        }

        return false
    }
}

class Cache {
    private val responseCache = mutableMapOf<Url, CacheEntity>()

    private fun cacheResponse(responseContainer: ResponseContainer) {
        val (_, request, response) = responseContainer
        if (response.statusCode != HttpStatusCode.OK && response.statusCode != HttpStatusCode.NotModified) return
        if (response.headers.noCache() || response.headers.noStore()) return

        response.headers.vary()?.let { vary ->
            cacheWithVary(request.url, vary, request.headers, response.build())
        }
    }

    private fun tryLoad(builder: RequestBuilder): Response? {
        val url = builder.url.build()
        return load(url, builder.headers) ?: run {
            if (builder.headers.onlyIfCached()) {
                error("Not found in cache")
            }

            null
        }
    }

    private fun load(url: Url, headers: HeadersBuilder): Response? {
        if (headers.noCache() || headers.noStore()) return null

        return responseCache[url]?.takeIf { it.match(headers) }?.cache
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
