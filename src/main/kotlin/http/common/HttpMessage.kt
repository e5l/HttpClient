package http.common

import org.jetbrains.ktor.util.ValuesMap

interface HttpMessage {
    val body: HttpMessageBody
    val headers: ValuesMap
}