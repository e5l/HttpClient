package http.request

import http.HttpCall
import http.pipeline.Pipeline
import http.pipeline.PipelinePhase

class HttpRequestPipeline : Pipeline<Any, HttpCall>(Route, Address, Send) {

    companion object Phases {
        val Route = PipelinePhase("Route")
        val Address = PipelinePhase("Address")
        val Send = PipelinePhase("Send")
    }
}
