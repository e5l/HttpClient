package http.pipeline

import http.request.HttpRequestPipeline
import http.response.HttpResponsePipeline

fun HttpClientScope.visit(
        before: (HttpClientScope) -> Unit = {},
        after: (HttpClientScope) -> Unit = {}
) {
    if (this is EmptyScope) {
        return
    }

    before(this)
    parent.visit(before, after)
    after(this)
}

fun HttpClientScope.buildResponsePipeline(): HttpResponsePipeline = HttpResponsePipeline().apply {
    visit(after = { merge(it.responsePipeline) })
}

fun HttpClientScope.buildRequestPipeline(): HttpRequestPipeline = HttpRequestPipeline().apply {
    visit(after = { merge(it.requestPipeline) })
}

fun HttpClientScope.config(block: HttpClientScope.() -> Unit): HttpClientScope {
    return CallScope(this).apply(block)
}