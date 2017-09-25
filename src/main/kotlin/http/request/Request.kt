package http.request

import http.call.HttpClientCall


class Request(val call: HttpClientCall, val pipeline: RequestPipeline, val data: RequestData)

fun request(block: RequestDataBuilder.() -> Unit): RequestDataBuilder = RequestDataBuilder().apply(block)
