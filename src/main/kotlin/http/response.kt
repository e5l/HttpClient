package http

import http.call.HttpClientCall

suspend fun HttpClientCall.receiveText(): String = makeRequest<String>()
