package http.tests.utils

import org.jetbrains.ktor.host.ApplicationHost
import org.junit.After
import org.junit.Before
import java.util.concurrent.TimeUnit

abstract class TestWithKtor {
    abstract val server: ApplicationHost

    @Before
    fun startServer() {
        server.start()
    }

    @After
    fun stopServer() {
        server.stop(0, 0, TimeUnit.SECONDS)
    }
}
