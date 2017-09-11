package http.request

import http.call.HttpClientCall
import org.jetbrains.ktor.pipeline.Pipeline
import org.jetbrains.ktor.pipeline.PipelinePhase

class RequestPipeline : Pipeline<Any, HttpClientCall>(Route, Address, State, Content, Send) {
    companion object Phases {
        val Route = PipelinePhase("Route")
        val Address = PipelinePhase("Address")
        val State = PipelinePhase("State")
        val Content = PipelinePhase("Content")
        val Send = PipelinePhase("Send")
    }
}
