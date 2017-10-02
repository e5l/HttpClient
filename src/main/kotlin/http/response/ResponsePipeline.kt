package http.response

import http.pipeline.ClientScope
import http.pipeline.Pipeline
import http.pipeline.PipelinePhase
import http.request.Request
import kotlin.reflect.KClass

class ResponsePipeline : Pipeline<ResponseContainer, ClientScope>(Receive, Parse, Transform, State, After) {
    companion object Phases {
        val Receive = PipelinePhase("Receive")
        val Parse = PipelinePhase("Parse")
        val Transform = PipelinePhase("Transform")
        val State = PipelinePhase("State")
        val After = PipelinePhase("After")
    }
}

data class ResponseContainer(val expectedType: KClass<*>, val request: Request, val response: ResponseBuilder)
