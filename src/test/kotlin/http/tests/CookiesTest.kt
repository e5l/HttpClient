package http.tests

import http.HttpClient
import http.backend.jvm.ApacheBackend
import http.call.get
import http.pipeline.config
import http.response.HttpResponseData
import http.tests.utils.TestWithKtor
import kotlinx.coroutines.experimental.runBlocking
import org.jetbrains.ktor.host.ApplicationHost
import org.jetbrains.ktor.host.embeddedServer
import org.jetbrains.ktor.http.*
import org.jetbrains.ktor.netty.Netty
import org.jetbrains.ktor.response.respond
import org.jetbrains.ktor.response.respondText
import org.jetbrains.ktor.routing.get
import org.jetbrains.ktor.routing.routing
import org.junit.Test

/* TODO:
 *   1. client cookie: configure client
 *   2.
 */

class CookiesTest : TestWithKtor() {
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
        val client = HttpClient(ApacheBackend)

        val response = runBlocking { client.get(port = 8080) }
        val cookies = response.newCookies()
        client.close()
    }

    @Test
    fun testUpdate() {
        val client = HttpClient(ApacheBackend).config {
//            addCookie(Cookie(...))
//            addCookie("user", "vasya")
//            cookiePolicy(Cookies.FREEZE)
        }

        val response = runBlocking { client.get(path = "update-user-id", port = 8080) }
        val cookies = response.newCookies()

//        client.close()
    }

    @Test
    fun session() {
    }
}

private fun HttpResponseData.newCookies(): List<Cookie>? = headers.getAll(HttpHeaders.SetCookie)?.map { parseServerSetCookieHeader(it) }
