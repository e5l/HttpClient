package http.utils

import org.jetbrains.ktor.cio.ReadChannel
import org.jetbrains.ktor.cio.WriteChannel

sealed class HttpMessageBody
class ReadChannelBody(val channel: ReadChannel) : HttpMessageBody()
class WriteChannelBody(val block: (WriteChannel) -> Unit) : HttpMessageBody()

object EmptyBody : HttpMessageBody()