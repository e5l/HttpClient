package http.tests

import http.HttpClient
import http.backend.jvm.ApacheBackend
import http.features.Cookies
import http.features.cookies
import http.features.install
import http.get
import http.pipeline.config
import http.tests.utils.TestWithKtor
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.ktor.host.ApplicationHost
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.Cookie
import org.jetbrains.ktor.http.HttpStatusCode
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import org.junit.Test


class CookiesTest : TestWithKtor() {
    private val HOST = "localhost"

    override val server: ApplicationHost = embeddedServer(Netty, 8080) {
        routing {
            get("/") {
                val cookie = Cookie("hello-cookie", "my-awesome-value")
                call.response.cookies.append(cookie)

                call.respond("Done")
            }
            get("/update-user-id") {
                val id = call.request.cookies["id"]?.toInt() ?: let {
                    call.response.status(HttpStatusCode.Forbidden)
                    call.respondText("Forbidden")
                    return@get
                }

                val cookie = Cookie("id", (id + 1).toString())
                call.response.cookies.append(cookie)

                call.respond("Done")
            }
        }
    }

    @Test
    fun testAccept() {
        val client = HttpClient(ApacheBackend).config {
            install(Cookies)
        }

        val response = runBlocking { client.get(HOST, port = 8080) }
        client.cookies(HOST).let {
            assert(it.size == 1)
            assert(it["hello-cookie"]!!.value == "my-awesome-value")
        }

        client.close()
    }

    @Test
    fun testUpdate() {
        val client = HttpClient(ApacheBackend).config {
            install(Cookies) {
                set(HOST, Cookie("id", "1"))
            }
        }

        fun getId() = client.cookies(HOST)["id"]?.value?.toInt()!!

        for (i in 1..10) {
            val before = getId()
            val response = runBlocking { client.get(path = "update-user-id", port = 8080) }
            assert(getId() == before + 1)
        }

        client.close()
    }
}

