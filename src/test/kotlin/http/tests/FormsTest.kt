package http.tests

import http.HttpClient
import http.backend.jvm.ApacheBackend
import http.call.call
import http.features.FormData
import http.features.HttpForms
import http.pipeline.config
import http.receiveText
import http.tests.utils.TestWithKtor
import http.utils.url
import io.ktor.host.ApplicationHost
import io.ktor.host.embeddedServer
import io.ktor.netty.Netty
import io.ktor.routing.routing
import io.ktor.routing.get
import io.ktor.response.*
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.serialization.Serializable
import org.junit.Test


class FormsTest : TestWithKtor() {
    override val server: ApplicationHost = embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                val (username, password) = with(context.request.queryParameters) {
                    get("username") to get("password")
                }

                if (username == "vasya" && password == "pupkin")
                    context.respondText("OK")
                else
                    context.respondText("FAIL")
            }
        }
    }

    @Serializable
    data class LoginForm(val username: String, val password: String)

    @Test
    fun submitGetForm() {
        val client = HttpClient(ApacheBackend).config {
            install(HttpForms)
        }

        val response = runBlocking {
            val call = client.call({
                url(port = 8080)
                payload = FormData(LoginForm("vasya", "pupkin"))
            })

            call.receiveText()
        }

        assert(response == "OK")
    }
}
