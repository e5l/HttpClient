package http.features.json

import kotlinx.serialization.json.JSON
import kotlinx.serialization.serializer
import kotlin.reflect.KClass

class DefaultSerializer : JsonSerializer {
    override fun write(data: Any): String = JSON.stringify(data)

    override fun read(type: KClass<*>, data: String): Any = JSON.parse(type.serializer(), data)
}