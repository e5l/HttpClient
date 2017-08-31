package http.examples

import http.HttpClient
import http.backend.jvm.ApacheBackend

/*
fun testComplex(client: HttpClient) {
    client.call {
        protocol = Http
        protocol = Https
        address = ""
        port = ""
        url = "google.com"
        path = "/"
        headers {
            acceptEncoding = ""
            acceptLanguage = ""
            accept = ""
            userAgent = ""
        }
        contentType {}
        method = Get {
            queryParams = ""
        }
        method = Post {
            body = ""
        }
        dns = {}
        proxy = {}
    }
}
*/

fun requestApi() {
    val client = HttpClient(ApacheBackend)
}