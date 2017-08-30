package http.backend.jvm

import http.backend.HttpBackend
import http.backend.HttpRequestData
import http.backend.HttpResponseData
import kotlinx.coroutines.experimental.suspendCancellableCoroutine
import org.apache.http.HttpResponse
import org.apache.http.concurrent.FutureCallback
import org.apache.http.impl.nio.client.HttpAsyncClients
import org.apache.http.util.EntityUtils
import org.jetbrains.ktor.http.HttpStatusCode
import java.net.URI

class ApacheBackend : HttpBackend {
    private val backend = HttpAsyncClients.createDefault()

    init {
        backend.start()
    }

    suspend override fun makeRequest(data: HttpRequestData): HttpResponseData {
        val builder = org.apache.http.client.methods.RequestBuilder.create(data.method.value)
        builder.uri = URI(data.url)

        data.headers.entries().forEach { (name, values) ->
            values.forEach { value -> builder.addHeader(name, value) }
        }

        val request = builder.build()

        val response = suspendCancellableCoroutine<HttpResponse> { continuation ->
            backend.execute(request, object : FutureCallback<HttpResponse> {
                override fun failed(exception: Exception) {
                    continuation.resumeWithException(exception)
                }

                override fun cancelled() {
                    continuation.cancel()
                }

                override fun completed(result: HttpResponse) {
                    continuation.resume(result)
                }

            })
        }

        // blocking
        val code = HttpStatusCode.fromValue(response.statusLine.statusCode)
        val body = EntityUtils.toString(response.entity)
        return HttpResponseData(code, body)
    }

    override fun close() {
        backend.close()
    }
}
