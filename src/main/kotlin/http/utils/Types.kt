package http.utils

inline fun <reified T> Any?.safeAs(): T? = this as? T
