package http.utils

import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.http.charset
import org.jetbrains.ktor.util.ValuesMap
import org.jetbrains.ktor.util.ValuesMapBuilder
import java.nio.charset.Charset
import java.util.*

typealias Headers = ValuesMap

typealias HeadersBuilder = ValuesMapBuilder

/* Cache headers */


/* Common */

/**
 * Returns cache life time in seconds. Overwrite other invalidation methods if preset.
 */

fun Headers.maxAge(): Int? = TODO()

fun Headers.noCache(): Boolean = TODO()
fun HeadersBuilder.noCache(): Boolean = TODO()

fun Headers.noStore(): Boolean = TODO()
fun HeadersBuilder.noStore(): Boolean = TODO()

fun Headers.noTransform(): Boolean = TODO()

/* Request */

fun Headers.maxStale(): List<Int>? = TODO()

fun Headers.minFresh(): Int? = TODO()

fun HeadersBuilder.onlyIfCached(): Boolean = TODO()

fun Headers.expires(): Date = TODO()

fun HeadersBuilder.userAgent(content: String) = set(HttpHeaders.UserAgent, content)

/* Response */

fun Headers.date(): Date? = TODO()

fun Headers.mustRevalidate(): Boolean = TODO()

fun Headers.public(): Boolean = TODO()

fun Headers.private(): Boolean = TODO()

fun Headers.proxyRevalidate(): Boolean = TODO()

fun Headers.sMaxAge(): Int? = TODO()

fun HeadersBuilder.vary(): List<String>? = getAll(HttpHeaders.Vary)?.let { headers ->
    headers.flatMap { it.split(",") }.map { it.trim() }
}


fun HeadersBuilder.charset(): Charset? = get(HttpHeaders.ContentType)?.let { ContentType.parse(it).charset() }
