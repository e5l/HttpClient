package http.pipeline

import http.utils.safeAs


inline fun <reified NewSubject : Any, Context : Any> Pipeline<*, Context>.intercept(
        phase: PipelinePhase,
        crossinline block: PipelineContext<NewSubject, Context>.(NewSubject) -> Unit) {
    intercept(phase) interceptor@ { subject ->
        subject as? NewSubject ?: return@interceptor
        safeAs<PipelineContext<NewSubject, Context>>()?.block(subject)
    }
}