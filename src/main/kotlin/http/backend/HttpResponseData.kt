package http.backend

import org.jetbrains.ktor.http.HttpStatusCode

class HttpResponseData(val statusCode: HttpStatusCode, val body: String)