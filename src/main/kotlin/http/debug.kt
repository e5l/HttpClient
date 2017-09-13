package http

import http.call.HttpClientCall


suspend fun HttpClientCall.debug(): String = receiveText().let { body ->
    "$response: ${body.take(42)}"
}
