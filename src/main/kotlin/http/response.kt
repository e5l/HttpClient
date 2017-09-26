package http

import http.call.HttpClientCall
import http.call.receive

suspend fun HttpClientCall.receiveText(): String = receive<String>()
