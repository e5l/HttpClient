package http.utils

import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.http.charset
import org.jetbrains.ktor.util.ValuesMap
import org.jetbrains.ktor.util.ValuesMapBuilder
import java.nio.charset.Charset

typealias Headers = ValuesMap

typealias HeadersBuilder = ValuesMapBuilder

fun HeadersBuilder.vary(): List<String>? = getAll(HttpHeaders.Vary)?.let { headers ->
    headers.flatMap { it.split(",") }.map { it.trim() }
}

fun HeadersBuilder.charset(): Charset? = get(HttpHeaders.ContentType)?.let { ContentType.parse(it).charset() }
fun HeadersBuilder.userAgent(content: String) = set(HttpHeaders.UserAgent, content)
