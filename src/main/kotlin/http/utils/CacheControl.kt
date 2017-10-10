package http.utils

import org.jetbrains.ktor.http.HttpHeaders

abstract class RequestCacheControl {
    abstract val maxAge: Int?
    abstract val maxStale: Int?
    abstract val minFresh: Int?
    abstract val noCache: Boolean
    abstract val noStore: Boolean
    abstract val noTransform: Boolean
    abstract val onlyIfCached: Boolean
}


abstract class ResponseCacheControl {
    abstract val mustRevalidate: Boolean
    abstract val noCache: Boolean
    abstract val noStore: Boolean
    abstract val noTransform: Boolean
    abstract val public: Boolean
    abstract val private: Boolean
    abstract val proxyRevalidate: Boolean
    abstract val maxAge: Int?
    abstract val sMaxAge: Int?
}

class RequestCacheControlFromList(private val cacheControl: List<String>) : RequestCacheControl() {
    override val maxAge: Int? = cacheControl.intProperty(CacheControl.MAX_AGE)

    override val maxStale: Int? = cacheControl.intProperty(CacheControl.MAX_STALE)

    override val minFresh: Int? = cacheControl.intProperty(CacheControl.MIN_FRESH)

    override val noCache: Boolean = cacheControl.booleanProperty(CacheControl.NO_CACHE)

    override val noStore: Boolean = cacheControl.booleanProperty(CacheControl.NO_STORE)

    override val noTransform: Boolean = cacheControl.booleanProperty(CacheControl.NO_TRANSFORM)

    override val onlyIfCached: Boolean = cacheControl.booleanProperty(CacheControl.ONLY_IF_CACHED)
}

class ResponseCacheControlFromList(private val cacheControl: List<String>) : ResponseCacheControl() {
    override val mustRevalidate: Boolean = cacheControl.booleanProperty(CacheControl.MUST_REVALIDATE)

    override val noCache: Boolean = cacheControl.booleanProperty(CacheControl.NO_CACHE)

    override val noStore: Boolean = cacheControl.booleanProperty(CacheControl.NO_STORE)

    override val noTransform: Boolean = cacheControl.booleanProperty(CacheControl.NO_TRANSFORM)

    override val public: Boolean = cacheControl.booleanProperty(CacheControl.PUBLIC)

    override val private: Boolean = cacheControl.booleanProperty(CacheControl.PRIVATE)

    override val proxyRevalidate: Boolean = cacheControl.booleanProperty(CacheControl.PROXY_REVALIDATE)

    override val maxAge: Int? = cacheControl.intProperty(CacheControl.MAX_AGE)

    override val sMaxAge: Int? = cacheControl.intProperty(CacheControl.S_MAX_AGE)
}

private fun List<String>.booleanProperty(key: String): Boolean = contains(key)

private fun List<String>.intProperty(key: String): Int? =
        find { it.startsWith(key) }?.split("=")?.getOrNull(1)?.toInt()

fun Headers.computeRequestCacheControl(): RequestCacheControl {
    val rawHeader = getAll(HttpHeaders.CacheControl) ?: listOf()
    return RequestCacheControlFromList(rawHeader)
}

fun HeadersBuilder.computeRequestCacheControl(): RequestCacheControl {
    val rawHeader = getAll(HttpHeaders.CacheControl) ?: listOf()
    return RequestCacheControlFromList(rawHeader)
}

fun Headers.computeResponseCacheControl(): ResponseCacheControl {
    val rawHeader = getAll(HttpHeaders.CacheControl) ?: listOf()
    return ResponseCacheControlFromList(rawHeader)
}

fun HeadersBuilder.computeResponseCacheControl(): ResponseCacheControl {
    val rawHeader = getAll(HttpHeaders.CacheControl) ?: listOf()
    return ResponseCacheControlFromList(rawHeader)
}

object CacheControl {
    val MAX_AGE = "max-age"
    val MIN_FRESH = "min-fresh"
    val ONLY_IF_CACHED = "only-if-cached"

    val MAX_STALE = "max-stale"
    val NO_CACHE = "no-cache"
    val NO_STORE = "no-store"
    val NO_TRANSFORM = "no-transform"

    val MUST_REVALIDATE = "must-revalidate"
    val PUBLIC = "private"
    val PRIVATE = "private"
    val PROXY_REVALIDATE = "proxy-revalidate"
    val S_MAX_AGE = "s-maxage"
}

