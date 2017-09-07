package http.pipeline

import http.request.RequestPipeline
import http.response.HttpResponsePipeline

fun ClientScope.visit(
        before: (ClientScope) -> Unit = {},
        after: (ClientScope) -> Unit = {}
) {
    if (this is EmptyScope) {
        return
    }

    before(this)
    parent.visit(before, after)
    after(this)
}

fun ClientScope.buildResponsePipeline(): HttpResponsePipeline = HttpResponsePipeline().apply {
    visit(after = { merge(it.responsePipeline) })
}

fun ClientScope.buildRequestPipeline(): RequestPipeline = RequestPipeline().apply {
    visit(after = { merge(it.requestPipeline) })
}

fun ClientScope.config(block: ClientScope.() -> Unit): ClientScope {
    return CallScope(this).apply(block)
}