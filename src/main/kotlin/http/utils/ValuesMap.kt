package http.utils

import org.jetbrains.ktor.util.ValuesMap
import org.jetbrains.ktor.util.ValuesMapBuilder


fun ValuesMapBuilder.appendAll(valuesMap: ValuesMapBuilder): ValuesMapBuilder = apply {
    valuesMap.entries().forEach { (name, values) ->
        appendAll(name, values)
    }
}

fun valuesMapBuilderOf(builder: ValuesMapBuilder): ValuesMapBuilder =
        ValuesMapBuilder().appendAll(builder)

fun valuesOf(builder: ValuesMapBuilder): ValuesMap = valuesMapBuilderOf(builder).build()
