package http.request

import org.jetbrains.ktor.http.ContentType
import org.jetbrains.ktor.http.HttpHeaders

val Request.host get() = url.host
val RequestBuilder.host get() = url.host

fun RequestBuilder.header(key: String, value: String) = headers.append(key, value)
fun RequestBuilder.accept(contentType: ContentType) = headers.append(HttpHeaders.Accept, contentType.toString())

