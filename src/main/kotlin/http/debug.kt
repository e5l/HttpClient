package http

import http.response.HttpResponseData

fun HttpResponseData.debug(): String = bodyText().let { body ->
    "$statusCode: ${body.take(42)}"
}

