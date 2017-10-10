package http.response

import http.pipeline.HttpClientScope
import http.pipeline.Pipeline
import http.pipeline.PipelinePhase
import http.request.HttpRequest
import kotlin.reflect.KClass

class HttpResponsePipeline : Pipeline<HttpResponseContainer, HttpClientScope>(Receive, Parse, Transform, State, After) {
    companion object Phases {
        val Receive = PipelinePhase("Receive")
        val Parse = PipelinePhase("Parse")
        val Transform = PipelinePhase("Transform")
        val State = PipelinePhase("State")
        val After = PipelinePhase("After")
    }
}

data class HttpResponseContainer(val expectedType: KClass<*>, val request: HttpRequest, val response: HttpResponseBuilder)
