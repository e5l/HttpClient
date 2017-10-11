package http.utils

import http.request.HttpRequestBuilder
import http.response.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.charset
import io.ktor.util.ValuesMap
import io.ktor.util.ValuesMapBuilder
import java.nio.charset.Charset
import java.text.SimpleDateFormat
import java.util.*

typealias Headers = ValuesMap

typealias HeadersBuilder = ValuesMapBuilder

val HTTP_DATE_FORMAT: SimpleDateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.US).apply {
    timeZone = TimeZone.getTimeZone("GMT")
}

fun HeadersBuilder.charset(): Charset? = get(HttpHeaders.ContentType)?.let { ContentType.parse(it).charset() }
fun HeadersBuilder.userAgent(content: String) = set(HttpHeaders.UserAgent, content)

fun HttpResponse.vary(): List<String>? = headers[HttpHeaders.Vary]?.split(",")?.map { it.trim() }

fun HttpRequestBuilder.ifModifiedSince(date: Date) =
        headers.set(HttpHeaders.IfModifiedSince, HTTP_DATE_FORMAT.format(date))

fun HttpRequestBuilder.ifMatch(value: String) = headers.set(HttpHeaders.IfMatch, value)

fun HttpResponse.lastModified(): Date? = headers[HttpHeaders.LastModified]?.let { HTTP_DATE_FORMAT.parse(it) }
fun HttpResponse.etag(): String? = headers[HttpHeaders.ETag]
