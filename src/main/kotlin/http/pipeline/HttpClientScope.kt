package http.pipeline

import http.request.HttpRequestPipeline
import http.response.HttpResponsePipeline
import org.jetbrains.ktor.util.Attributes
import java.io.Closeable

sealed class HttpClientScope : Closeable {
    abstract val attributes: Attributes

    abstract val requestPipeline: HttpRequestPipeline
    abstract val responsePipeline: HttpResponsePipeline
}

open class HttpCallScope : HttpClientScope() {
    override fun close() {}

    override val attributes = Attributes()
    override val requestPipeline: HttpRequestPipeline = HttpRequestPipeline()

    override val responsePipeline: HttpResponsePipeline = HttpResponsePipeline()
}
