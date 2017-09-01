package http.response

import http.HttpCall
import org.jetbrains.ktor.pipeline.Pipeline
import org.jetbrains.ktor.pipeline.PipelinePhase
import kotlin.reflect.KClass

data class ResponseContainer(val expectedType: KClass<*>, val value: Any)

class HttpResponsePipeline : Pipeline<ResponseContainer, HttpCall>(Transform) {
    companion object Phases {
        val Transform = PipelinePhase("Transform")
    }
}