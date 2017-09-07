package http

import http.call.HttpClientCall


suspend fun HttpClientCall.debug(): String = bodyText().let { body ->
    "${response}: ${body.take(42)}"
}

