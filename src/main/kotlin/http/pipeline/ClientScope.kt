package http.pipeline

import http.request.RequestPipeline
import http.response.ResponsePipeline
import org.jetbrains.ktor.util.Attributes
import java.io.Closeable

sealed class ClientScope : Closeable {
    abstract val attributes: Attributes

    abstract val parent: ClientScope
    abstract val requestPipeline: RequestPipeline
    abstract val responsePipeline: ResponsePipeline
}

class EmptyScope : ClientScope() {
    override val attributes = Attributes()

    override val parent = this
    override val requestPipeline = RequestPipeline()
    override val responsePipeline = ResponsePipeline()

    override fun close() {}
}

open class CallScope(final override val parent: ClientScope) : ClientScope() {
    override fun close() {}

    override val attributes = Attributes()
    override val requestPipeline: RequestPipeline = RequestPipeline().apply {
        merge(parent.requestPipeline)
    }

    override val responsePipeline: ResponsePipeline = ResponsePipeline().apply {
        merge(parent.responsePipeline)
    }
}
