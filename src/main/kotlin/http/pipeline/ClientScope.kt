package http.pipeline

import http.request.RequestPipeline
import http.response.ResponsePipeline
import org.jetbrains.ktor.util.Attributes
import java.io.Closeable

sealed class ClientScope : Closeable {
    abstract val attributes: Attributes

    abstract val requestPipeline: RequestPipeline
    abstract val responsePipeline: ResponsePipeline
}

open class CallScope : ClientScope() {
    override fun close() {}

    override val attributes = Attributes()
    override val requestPipeline: RequestPipeline = RequestPipeline()

    override val responsePipeline: ResponsePipeline = ResponsePipeline()
}
