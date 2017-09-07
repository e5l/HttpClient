package http.response

import http.call.HttpClientCall
import org.jetbrains.ktor.pipeline.Pipeline
import org.jetbrains.ktor.pipeline.PipelinePhase
import kotlin.reflect.KClass

data class ResponseContainer(val expectedType: KClass<*>, val response: Any)

class HttpResponsePipeline : Pipeline<ResponseContainer, HttpClientCall>(Transform) {
    companion object Phases {
        val Transform = PipelinePhase("Transform")
    }
}