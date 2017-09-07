package http.response

import http.call.HttpClientCall
import org.jetbrains.ktor.http.response.HttpResponse
import org.jetbrains.ktor.util.ValuesMap

class Response(
        val call: HttpClientCall,
        val pipeline: HttpResponsePipeline
) : HttpResponse {
    override lateinit var headers: ValuesMap

    val builder: ResponseBuilder = ResponseBuilder()
}

inline suspend fun <reified T> HttpClientCall.makeResponse(responseData: Any): ResponseContainer =
        response.pipeline.execute(this, ResponseContainer(T::class, responseData))
