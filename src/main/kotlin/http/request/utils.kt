package http.request

import http.call.HttpClientCall
import org.jetbrains.ktor.http.HttpHeaders
import org.jetbrains.ktor.util.ValuesMapBuilder
import org.jetbrains.ktor.util.toMap

fun HttpClientCall.parameters(): String = request.data.queryParameters.toMap().entries.joinToString { (key, values) ->
    "$key=${if (values.size == 1) values.first() else values.joinToString(",")}"
}

fun ValuesMapBuilder.userAgent(value: String) {
    append(HttpHeaders.UserAgent, "Kotlin HttpClient")
}