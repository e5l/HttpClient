package http.features

import org.jetbrains.ktor.application.ApplicationCallPipeline
import org.jetbrains.ktor.application.ApplicationFeature
import org.jetbrains.ktor.util.AttributeKey

class Https {
    class Configuration

    companion object Feature : ApplicationFeature<ApplicationCallPipeline, Configuration, Https> {
        override val key: AttributeKey<Https>
            get() = TODO()

        override fun install(pipeline: ApplicationCallPipeline, configure: Configuration.() -> Unit): Https {
            TODO()
        }
    }
}