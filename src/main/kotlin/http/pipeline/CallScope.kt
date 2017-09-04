package http.pipeline

import http.request.HttpRequestPipeline
import http.response.HttpResponsePipeline

class CallScope(override val parent: HttpClientScope) : HttpClientScope {
    override val requestPipeline = HttpRequestPipeline()
    override val responsePipeline = HttpResponsePipeline()
}