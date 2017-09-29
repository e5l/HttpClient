package http.response

import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.Cookie
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.http.parseServerSetCookieHeader

fun ResponseBuilder.contentType(): ContentType? =
        headers[HttpHeaders.ContentType]?.let { ContentType.parse(it) }

fun ResponseBuilder.cookies(): List<Cookie> =
        headers.getAll(HttpHeaders.SetCookie)?.map { parseServerSetCookieHeader(it) } ?: listOf()
