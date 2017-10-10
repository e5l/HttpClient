package http.request

import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpHeaders

val HttpRequest.host get() = url.host
val HttpRequestBuilder.host get() = url.host

fun HttpRequestBuilder.header(key: String, value: String) = headers.append(key, value)
fun HttpRequestBuilder.accept(contentType: ContentType) = headers.append(HttpHeaders.Accept, contentType.toString())

