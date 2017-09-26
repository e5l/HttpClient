package http

import http.call.HttpClientCall


suspend fun HttpClientCall.debug(): String = receiveText().let { body ->
    "$request: ${body.take(42)}"
}
