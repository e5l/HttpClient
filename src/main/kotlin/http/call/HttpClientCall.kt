package http.call

import http.request.*
import http.response.*
import org.jetbrains.ktor.http.RequestConnectionPoint
import org.jetbrains.ktor.http.call.HttpCall
import org.jetbrains.ktor.http.request.HttpRequest
import org.jetbrains.ktor.http.response.HttpResponse
import org.jetbrains.ktor.util.Attributes
import org.jetbrains.ktor.util.ValuesMap

class HttpClientCall(
        requestPipeline: RequestPipeline,
        responsePipeline: ResponsePipeline,
        val requestBuilder: RequestDataBuilder = RequestDataBuilder(),
        val parameters: ValuesMap = ValuesMap.Empty
) {
    val request: Request = Request(this, requestPipeline)

    val response: Response = Response(this, responsePipeline)

    val attributes: Attributes = Attributes()
}
