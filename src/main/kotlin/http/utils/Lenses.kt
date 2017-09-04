package http.utils

interface Lens<in Container, Item> {
    operator fun get(container: Container): Item
    operator fun set(container: Container, item: Item)
}

fun <A, B, C> composeLenses(first: Lens<A, B>, second: Lens<B, C>) = object : Lens<A, C> {
    override fun get(container: A): C = second[first[container]]

    override fun set(container: A, item: C) {
        second[first[container]] = item
    }
}
