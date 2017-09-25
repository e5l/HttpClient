package http.request

import org.jetbrains.ktor.http.ContentType
import java.nio.charset.Charset

class Headers {
    fun entries(): Sequence<Pair<String, List<String>>> = TODO()

    operator fun get(contentType: String): List<String>? = TODO()
}

class HeadersBuilder {

    fun append(key: String, value: String): Unit = TODO()

    fun build(): Headers = TODO()

    fun takeFrom(headers: Headers) {}
}

fun Headers.charset(): Charset? = TODO()

fun HeadersBuilder.contentType(contentType: ContentType) {}
fun HeadersBuilder.userAgent(userAgent: String) {}

