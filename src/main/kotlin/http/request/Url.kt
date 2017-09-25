package http.request

class Parameters {
    fun entries(): Sequence<Pair<String, String>> = TODO()
}

class ParametersBuilder {
    fun append(key: String, value: String) {}
}

class Url(
        val scheme: String,
        val host: String,
        val port: Int,
        val path: String,
        val queryParameters: Parameters,
        val username: String?,
        val password: String?
) {
    override fun toString(): String = TODO()
}

class UrlBuilder {
    var scheme: String = "http"

    var host: String = "localhost"

    var port: Int = 80

    var path: String = ""

    var username: String? = null

    var password: String? = null

    var queryParameters = ParametersBuilder()

    fun takeFrom(url: Url) {}

    fun build(): Url = TODO()
}
