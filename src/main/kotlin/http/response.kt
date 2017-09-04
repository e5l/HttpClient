package http

import http.common.EmptyBody
import http.common.ReadChannelBody
import http.common.WriteChannelBody
import http.response.HttpResponseData
import org.jetbrains.ktor.cio.ByteBufferWriteChannel
import org.jetbrains.ktor.cio.toInputStream
import java.io.InputStreamReader
import java.nio.charset.Charset

// Todo: cache
fun HttpResponseData.bodyText(): String {
    val responseBody = body
    return when (responseBody) {
        is WriteChannelBody -> {
            val channel = ByteBufferWriteChannel()
            responseBody.block(channel)
            channel.toString(charset)
        }
        is ReadChannelBody -> InputStreamReader(responseBody.channel.toInputStream(), charset).readText()
        is EmptyBody -> ""
    }
}

// Todo: write feature to parse headers
val HttpResponseData.charset: Charset
    get() = headers
            .getAll("Content-Type")
            ?.flatMap { it.split(";") }
            ?.find { it.contains("charset") }
            ?.split("=")
            ?.let { Charset.forName(it[1]) }
            ?: Charset.defaultCharset()

