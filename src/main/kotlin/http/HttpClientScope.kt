package http

import http.request.HttpRequestPipeline
import http.response.HttpResponsePipeline

interface HttpClientScope {
    val parent: HttpClientScope

    val requestPipeline: HttpRequestPipeline
    val responsePipeline: HttpResponsePipeline
}

object EmptyScope : HttpClientScope {
    override val parent = this
    override val requestPipeline = HttpRequestPipeline()
    override val responsePipeline = HttpResponsePipeline()
}

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
