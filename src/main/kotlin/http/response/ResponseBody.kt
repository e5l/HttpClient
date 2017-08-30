package http.response

import java.io.InputStream

sealed class ResponseBody
class StreamBody(val stream: InputStream) : ResponseBody()
object EmptyBody : ResponseBody()