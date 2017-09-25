package http.call

import http.pipeline.ClientScope
import http.request.Request
import http.request.RequestData
import http.request.RequestDataBuilder
import http.response.Response
import org.jetbrains.ktor.util.Attributes

class HttpClientCall(scope: ClientScope, requestData: RequestData) {

    val request: Request = Request(this, scope.requestPipeline, requestData)

    val response: Response = Response(this, scope.responsePipeline)

    val attributes: Attributes = Attributes()
}

fun ClientScope.call(builder: RequestDataBuilder): HttpClientCall =
        HttpClientCall(this, builder.build())

fun ClientScope.call(block: RequestDataBuilder.() -> Unit): HttpClientCall =
        call(RequestDataBuilder().apply(block))
