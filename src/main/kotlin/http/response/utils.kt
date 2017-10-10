package http.response

import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.Cookie
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.http.parseServerSetCookieHeader

fun HttpResponseBuilder.contentType(): ContentType? =
        headers[HttpHeaders.ContentType]?.let { ContentType.parse(it) }

fun HttpResponseBuilder.cookies(): List<Cookie> =
        headers.getAll(HttpHeaders.SetCookie)?.map { parseServerSetCookieHeader(it) } ?: listOf()
