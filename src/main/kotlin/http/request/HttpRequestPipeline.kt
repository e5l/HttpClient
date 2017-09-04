package http.request

import http.call.HttpCall
import org.jetbrains.ktor.pipeline.Pipeline
import org.jetbrains.ktor.pipeline.PipelinePhase

class HttpRequestPipeline : Pipeline<Any, HttpCall>(Route, Address, State, Send) {

    companion object Phases {
        val Route = PipelinePhase("Route")
        val Address = PipelinePhase("Address")
        val State = PipelinePhase("State")
        val Send = PipelinePhase("Send")
    }
}
