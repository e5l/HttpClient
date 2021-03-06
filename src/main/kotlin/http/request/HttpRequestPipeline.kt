package http.request

import http.pipeline.HttpClientScope
import http.pipeline.Pipeline
import http.pipeline.PipelinePhase

class HttpRequestPipeline : Pipeline<Any, HttpClientScope>(Before, State, Transform, Render, Send) {
    companion object Phases {
        val Before = PipelinePhase("Before")
        val State = PipelinePhase("State")
        val Transform = PipelinePhase("Transform")
        val Render = PipelinePhase("Render")
        val Send = PipelinePhase("Send")
    }
}
