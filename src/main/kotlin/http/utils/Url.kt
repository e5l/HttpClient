package http.utils

import org.jetbrains.ktor.util.ValuesMap
import org.jetbrains.ktor.util.ValuesMapBuilder

typealias Parameters = ValuesMap

typealias ParametersBuilder = ValuesMapBuilder

data class Url(
        val scheme: String,
        val host: String,
        val port: Int,
        val path: String,
        val queryParameters: Parameters,
        val username: String?,
        val password: String?
)

class UrlBuilder {
    var scheme: String = "http"

    var host: String = "localhost"

    var port: Int = 80

    var path: String = ""

    var username: String? = null

    var password: String? = null

    var queryParameters = ParametersBuilder()

    fun build(): Url = Url(scheme, host, port, path, valuesOf(queryParameters), username, password)
}
