package http.response

import http.pipeline.ClientScope
import http.request.Request
import org.jetbrains.ktor.pipeline.Pipeline
import org.jetbrains.ktor.pipeline.PipelinePhase
import kotlin.reflect.KClass

class ResponsePipeline : Pipeline<ResponseContainer, ClientScope>(Before, Transform, Render, ContentEncoding, After) {
    companion object Phases {
        val Before = PipelinePhase("Before")
        val Transform = PipelinePhase("Transform")
        val Render = PipelinePhase("Render")
        val ContentEncoding = PipelinePhase("ContentEncoding")
        val After = PipelinePhase("After")
    }
}

data class ResponseContainer(val expectedType: KClass<*>, val request: Request, val response: ResponseBuilder)
