package http

import execute
import http.call.HttpClientCall
import http.common.EmptyBody
import http.common.HttpMessageBody
import http.common.ReadChannelBody
import http.common.WriteChannelBody
import http.response.Response
import org.jetbrains.ktor.cio.ByteBufferWriteChannel
import org.jetbrains.ktor.cio.toInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

fun HttpMessageBody.bodyText(charset: Charset = Charset.defaultCharset()): String {
    return when (this) {
        is WriteChannelBody -> {
            val channel = ByteBufferWriteChannel()
            block(channel)
            channel.toString(charset)
        }
        is ReadChannelBody -> InputStreamReader(channel.toInputStream(), charset).readText()
        is EmptyBody -> ""
    }
}

// Todo: cache
suspend fun HttpClientCall.bodyText(charset: Charset = Charset.defaultCharset())
        = execute<HttpMessageBody>().bodyText(charset)

// Move to
val Response.charset: Charset
    get() = headers
            .getAll("Content-Type")
            ?.flatMap { it.split(";") }
            ?.find { it.contains("charset") }
            ?.split("=")
            ?.let { Charset.forName(it[1]) }
            ?: Charset.defaultCharset()
