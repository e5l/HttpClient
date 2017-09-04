package http.response

import http.call.HttpCall
import http.request.HttpRequestData
import org.jetbrains.ktor.pipeline.Pipeline
import org.jetbrains.ktor.pipeline.PipelinePhase
import kotlin.reflect.KClass

data class ResponseContainer(val expectedType: KClass<*>, val request: HttpRequestData, val response: Any)

class HttpResponsePipeline : Pipeline<ResponseContainer, HttpCall>(Transform) {
    companion object Phases {
        val Transform = PipelinePhase("Transform")
    }
}