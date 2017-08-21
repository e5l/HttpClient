package http.pipeline

import org.jetbrains.ktor.pipeline.InvalidPhaseException
import org.jetbrains.ktor.util.Attributes

open class Pipeline<TSubject : Any, Call : Any>(vararg phases: PipelinePhase) {
    /**
     * Provides common place to store pipeline attributes
     */
    val attributes = Attributes()

    constructor(
            phase: PipelinePhase,
            interceptors: List<PipelineInterceptor<TSubject, Call>>
    ) : this(phase) {
        interceptors.forEach { intercept(phase, it) }
    }

    suspend fun execute(call: Call, subject: TSubject): TSubject = PipelineContext(call, interceptors(), subject).proceed()

    private class PhaseContent<TSubject : Any, Call : Any>(val phase: PipelinePhase,
                                                           val relation: PipelinePhaseRelation,
                                                           val interceptors: ArrayList<PipelineInterceptor<TSubject, Call>>) {
        override fun toString(): String = "Phase `${phase.name}`, ${interceptors.size} handlers"
    }

    sealed class PipelinePhaseRelation {
        class After(val relativeTo: PipelinePhase) : PipelinePhaseRelation()
        class Before(val relativeTo: PipelinePhase) : PipelinePhaseRelation()
        object Last : PipelinePhaseRelation()
    }

    private val phases = phases.mapTo(ArrayList<PhaseContent<TSubject, Call>>(phases.size)) {
        PhaseContent(it, PipelinePhaseRelation.Last, arrayListOf())
    }

    private var interceptorsQuantity = 0
    @Volatile
    private var interceptors: ArrayList<PipelineInterceptor<TSubject, Call>>? = null

    val items: List<PipelinePhase> get() = phases.map { it.phase }

    fun addPhase(phase: PipelinePhase) {
        if (phases.any { it.phase == phase }) return
        phases.add(PhaseContent(phase, PipelinePhaseRelation.Last, arrayListOf()))
        interceptors = null
    }

    fun insertPhaseAfter(reference: PipelinePhase, phase: PipelinePhase) {
        if (phases.any { it.phase == phase }) return
        val index = phases.indexOfFirst { it.phase == reference }
        if (index == -1)
            throw InvalidPhaseException("Phase $reference was not registered for this pipeline")
        phases.add(index + 1, PhaseContent(phase, PipelinePhaseRelation.After(reference), arrayListOf()))
        interceptors = null
    }

    fun insertPhaseBefore(reference: PipelinePhase, phase: PipelinePhase) {
        if (phases.any { it.phase == phase }) return
        val index = phases.indexOfFirst { it.phase == reference }
        if (index == -1)
            throw InvalidPhaseException("Phase $reference was not registered for this pipeline")
        phases.add(index, PhaseContent(phase, PipelinePhaseRelation.Before(reference), arrayListOf()))
        interceptors = null
    }

    private fun interceptors(): ArrayList<PipelineInterceptor<TSubject, Call>> {
        return interceptors ?: cacheInterceptors()
    }

    private fun cacheInterceptors(): ArrayList<PipelineInterceptor<TSubject, Call>> {
        val destination = ArrayList<PipelineInterceptor<TSubject, Call>>(interceptorsQuantity)
        for (phaseIndex in 0..phases.lastIndex) {
            val elements = phases[phaseIndex].interceptors
            for (elementIndex in 0..elements.lastIndex) {
                destination.add(elements[elementIndex])
            }
        }
        interceptors = destination
        return destination
    }

    open fun intercept(phase: PipelinePhase, block: PipelineInterceptor<TSubject, Call>) {
        val phaseContent = phases.firstOrNull { it.phase == phase }
                ?: throw InvalidPhaseException("Phase $phase was not registered for this pipeline")

        phaseContent.interceptors.add(block)
        interceptorsQuantity++
        interceptors = null
    }

    companion object {
        @JvmStatic
        private fun <T> ArrayList<T>.fastAddAll(list: ArrayList<T>) {
            ensureCapacity(size + list.size)
            for (index in 0..list.lastIndex) {
                add(list[index])
            }
        }

        @JvmStatic
        private fun <TSubject : Any, Call : Any> ArrayList<PhaseContent<TSubject, Call>>.findPhase(phase: PipelinePhase): PhaseContent<TSubject, Call>? {
            for (index in 0..lastIndex) {
                val localPhase = get(index)
                if (localPhase.phase == phase)
                    return localPhase
            }
            return null
        }
    }

    fun merge(from: Pipeline<TSubject, Call>) {
        if (from.phases.isEmpty())
            return
        if (phases.isEmpty()) {
            val fromPhases = from.phases
            @Suppress("LoopToCallChain")
            for (index in 0..fromPhases.lastIndex) {
                val fromContent = fromPhases[index]
                val interceptors = ArrayList<PipelineInterceptor<TSubject, Call>>(fromContent.interceptors.size)
                interceptors.fastAddAll(fromContent.interceptors)
                phases.add(PhaseContent(fromContent.phase, fromContent.relation, interceptors))
            }
            interceptorsQuantity += from.interceptorsQuantity
            interceptors = null
            return
        }

        val fromPhases = from.phases
        for (index in 0..fromPhases.lastIndex) {
            val fromContent = fromPhases[index]
            val phaseContent = phases.findPhase(fromContent.phase) ?: run {
                when (fromContent.relation) {
                    is PipelinePhaseRelation.Last -> addPhase(fromContent.phase)
                    is PipelinePhaseRelation.Before -> insertPhaseBefore(fromContent.relation.relativeTo, fromContent.phase)
                    is PipelinePhaseRelation.After -> insertPhaseAfter(fromContent.relation.relativeTo, fromContent.phase)
                }
                phases.first { it.phase == fromContent.phase }
            }
            phaseContent.interceptors.fastAddAll(fromContent.interceptors)
            interceptorsQuantity += fromContent.interceptors.size
        }
        interceptors = null
    }
}

inline suspend fun <Call : Any> Pipeline<Unit, Call>.execute(call: Call) = execute(call, Unit)

typealias PipelineInterceptor<TSubject, Call> = suspend PipelineContext<TSubject, Call>.(TSubject) -> Unit
