package http.call

import http.request.*
import http.response.*
import org.jetbrains.ktor.http.call.HttpCall
import org.jetbrains.ktor.util.Attributes
import org.jetbrains.ktor.util.ValuesMap

class HttpClientCall(
        requestPipeline: RequestPipeline,
        responsePipeline: HttpResponsePipeline,
        val requestBuilder: RequestBuilder
) : HttpCall {
    override val request: Request by lazy {
        requestBuilder.build(this, requestPipeline)
    }

    override val response: Response = Response(this, responsePipeline)

    override val parameters: ValuesMap = ValuesMap.Empty

    override val attributes: Attributes = Attributes()
}
