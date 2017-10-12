package http.pipeline

import http.request.HttpRequestPipeline
import http.response.HttpResponsePipeline
import io.ktor.util.Attributes
import java.io.Closeable

sealed class HttpClientScope : Closeable {
    abstract val attributes: Attributes

    abstract val requestPipeline: HttpRequestPipeline
    abstract val responsePipeline: HttpResponsePipeline

    override fun close() {}
}

object EmptyScope : HttpClientScope() {
    override val attributes: Attributes = Attributes()
    override val requestPipeline: HttpRequestPipeline = HttpRequestPipeline()
    override val responsePipeline: HttpResponsePipeline = HttpResponsePipeline()
}

open class HttpCallScope(val parent: HttpClientScope) : HttpClientScope() {

    override fun close() {
        parent.close()
    }

    override val attributes = Attributes()
    override val requestPipeline: HttpRequestPipeline = HttpRequestPipeline()

    override val responsePipeline: HttpResponsePipeline = HttpResponsePipeline()
}
