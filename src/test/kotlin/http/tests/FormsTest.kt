package http.tests

import http.HttpClient
import http.backend.jvm.ApacheBackend
import http.call.call
import http.receiveText
import http.tests.utils.TestWithKtor
import http.url
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.serialization.Serializable
import org.jetbrains.ktor.host.ApplicationHost
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import org.junit.Test


class FormsTest : TestWithKtor() {
    override val server: ApplicationHost = embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                val (username, password) = with(call.request.queryParameters) {
                    get("username") to get("password")
                }

                if (username == "vasya" && password == "pupkin")
                    call.respondText("OK")
                else
                    call.respondText("FAIL")
            }
        }
    }

    @Serializable
    data class LoginForm(val username: String, val password: String)

    @Test
    fun submitGetForm() {
        val client = HttpClient(ApacheBackend)


        val response = runBlocking {
            val call = client.call({
                url(port = 8080)
                payload = LoginForm("vasya", "pupkin")
            })

            call.receiveText()
        }

        assert(response == "OK")
    }
}
