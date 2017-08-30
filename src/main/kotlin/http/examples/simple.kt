package http.examples

/*
suspend fun HttpRequest.readBody(): String = TODO()

suspend fun singleGet(client: HttpClient) {
    val response = client.get("http://google.com").readBody()
}

suspend fun simplePost(client: HttpClient) {
    // using json serialization by default
    val vasya = { TODO() }
    val response = client.post("qwerty.com/createUser", vasya)
}

suspend fun batchCall(client: HttpClient) {
    fun String.hrefs(): List<String> = TODO()

    val resource = client.get("http://github.com")

    val result = mutableListOf(resource.readBody())
    var queue = result.first().hrefs()

    while (queue.isNotEmpty()) {
        val bodies = queue.map { resource.get(it).readBody() }
        result.addAll(bodies)
        queue = bodies.flatMap { it.hrefs() }
    }
}
*/
