package http

import http.request.HttpRequestPipeline
import http.response.HttpResponsePipeline
import kotlinx.coroutines.experimental.runBlocking

class HttpClientSession(val client: HttpClient) : HttpClientScope {
    override val parent = client

    override val requestPipeline = HttpRequestPipeline()
    override val responsePipeline = HttpResponsePipeline()
}

fun HttpClient.session(block: suspend HttpClientSession.() -> Unit) {
    val session = HttpClientSession(this)

    runBlocking {
        session.block()
    }
}

fun HttpClientSession.connect(
        resource: String,
        block: HttpConnection.() -> Unit = {}
): HttpConnection = HttpConnection(this, resource).apply(block)