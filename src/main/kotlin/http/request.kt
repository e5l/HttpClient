package http

import http.call.HttpClientCall
import http.pipeline.ClientScope
import http.pipeline.buildRequestPipeline
import http.pipeline.buildResponsePipeline
import http.request.RequestDataBuilder
import http.response.makeResponse
import http.utils.safeAs
import org.jetbrains.ktor.http.HttpMethod

inline suspend fun <reified T> HttpClientCall.makeRequest(requestData: Any = Unit): T =
        request.pipeline.execute(this, requestData).let { responseData ->
            makeResponse<T>(responseData).response.safeAs<T>()
        } ?: error("Fail to process call: $this \n" + "Expected type: ${T::class}")

suspend inline fun <reified T> ClientScope.makeRequest(requestData: Any, builder: RequestDataBuilder): T =
        HttpClientCall(buildRequestPipeline(), buildResponsePipeline(), builder).makeRequest(requestData)

suspend inline fun <reified T> ClientScope.makeRequest(requestData: Any, block: RequestDataBuilder.() -> Unit): T =
        makeRequest(requestData, RequestDataBuilder().apply(block))

suspend inline fun <reified T> ClientScope.makeRequest(block: RequestDataBuilder.() -> Unit): T =
        makeRequest(Unit, block)

suspend inline fun <reified T> ClientScope.makeRequest(builder: RequestDataBuilder): T =
        makeRequest(Unit, builder)

suspend inline fun <reified T> ClientScope.get(
        scheme: String = "http", host: String = "localhost", port: Int = 80,
        path: String = "",
        payload: Any = Unit,
        block: RequestDataBuilder.() -> Unit
): T = makeRequest(payload) {
    url(scheme, host, port, path)
    method = HttpMethod.Get
    apply(block)
}

suspend inline fun <reified T> ClientScope.get(
        scheme: String = "http", host: String = "localhost", port: Int = 80,
        path: String = "",
        payload: Any = Unit
): T = get(scheme, host, port, path, payload, {})

suspend inline fun <reified T> ClientScope.post(
        scheme: String = "http", host: String = "localhost", port: Int = 80,
        path: String = "",
        payload: Any = Unit,
        block: RequestDataBuilder.() -> Unit
): T = makeRequest(payload) {
    url(scheme, host, port, path)
    method = HttpMethod.Post
    apply(block)
}

suspend inline fun <reified T> ClientScope.post(
        scheme: String = "http", host: String = "localhost", port: Int = 80,
        path: String = "",
        payload: Any = Unit
): T = post(scheme, host, port, path, payload, {})

