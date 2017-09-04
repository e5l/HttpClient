package http.pipeline

import http.request.HttpRequestPipeline
import http.response.HttpResponsePipeline
import org.jetbrains.ktor.util.Attributes
import java.io.Closeable

sealed class HttpClientScope : Closeable {
    abstract val attributes: Attributes

    abstract val parent: HttpClientScope
    abstract val requestPipeline: HttpRequestPipeline
    abstract val responsePipeline: HttpResponsePipeline
}

class EmptyScope : HttpClientScope() {
    override val attributes = Attributes()

    override val parent = this
    override val requestPipeline = HttpRequestPipeline()
    override val responsePipeline = HttpResponsePipeline()

    override fun close() {}
}

open class CallScope(final override val parent: HttpClientScope) : HttpClientScope() {
    override fun close() {
        parent.close()
    }

    override val attributes = Attributes()
    override val requestPipeline = HttpRequestPipeline()
    override val responsePipeline = HttpResponsePipeline()
}
