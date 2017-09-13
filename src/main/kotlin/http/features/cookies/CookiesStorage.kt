package http.features.cookies

import org.jetbrains.ktor.http.Cookie

interface CookiesStorage {
    operator fun get(host: String): Map<String, Cookie>?
    operator fun get(host: String, name: String): Cookie?
    operator fun set(host: String, cookie: Cookie)

    fun forEach(host: String, block: (Cookie) -> Unit)
}

open class AcceptAllCookiesStorage : CookiesStorage {
    private val data = mutableMapOf<String, MutableMap<String, Cookie>>()

    // todo: clone
    override fun get(host: String): Map<String, Cookie>? = synchronized(this) { data[host] }

    override operator fun get(host: String, name: String): Cookie? = synchronized(this) { data[host]?.get(name) }
    override operator fun set(host: String, cookie: Cookie) {
        synchronized(this) {
            init(host)
            data[host]?.set(cookie.name, cookie)
        }
    }



    override fun forEach(host: String, block: (Cookie) -> Unit) {
        synchronized(this) {
            init(host)
            data[host]?.values?.forEach(block)
        }
    }

    private fun init(host: String) {
        if (!data.containsKey(host)) {
            data[host] = mutableMapOf()
        }
    }
}

class ConstantCookieStorage(vararg cookies: Cookie) : CookiesStorage {
    private val storage: Map<String, Cookie> = cookies.map { it.name to it }.toMap()

    override fun get(host: String): Map<String, Cookie>? = storage

    override fun get(host: String, name: String): Cookie? = storage[name]

    override fun set(host: String, cookie: Cookie) {}

    override fun forEach(host: String, block: (Cookie) -> Unit) {
        storage.values.forEach(block)
    }
}
