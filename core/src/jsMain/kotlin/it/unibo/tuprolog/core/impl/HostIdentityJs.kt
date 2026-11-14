package it.unibo.tuprolog.core.impl

private val identities: dynamic = js("new WeakMap()")

private var nextIdentity: Int = 0

private fun isObjectLike(value: Any): Boolean = jsTypeOf(value).let { it == "object" || it == "function" }

private fun identityOf(value: Any): Int {
    val existing: Int? = identities.get(value).unsafeCast<Int?>()
    if (existing != null) return existing
    val identity = nextIdentity++
    identities.set(value, identity)
    return identity
}

// Object.is: like ===, but NaN is the same as itself and +0 is not the same as -0
internal actual fun sameHost(
    a: Any,
    b: Any,
): Boolean = js("Object").`is`(a, b).unsafeCast<Boolean>()

internal actual fun hostIdentityHashCode(value: Any): Int =
    if (isObjectLike(value)) identityOf(value) else value.hashCode()

internal actual fun compareHostIdentities(
    a: Any,
    b: Any,
): Int {
    if (sameHost(a, b)) return 0
    val aIsObject = isObjectLike(a)
    val bIsObject = isObjectLike(b)
    return when {
        aIsObject && bIsObject -> identityOf(a).compareTo(identityOf(b))
        // primitives (compared by value, as JS does) come before objects (compared by identity)
        aIsObject -> 1
        bIsObject -> -1
        else -> comparePrimitives(a, b)
    }
}

private fun comparePrimitives(
    a: Any,
    b: Any,
): Int {
    val typeA = jsTypeOf(a)
    val typeB = jsTypeOf(b)
    if (typeA != typeB) return typeA.compareTo(typeB)
    // shortcut: distinct symbols with the same description compare as 0, add a symbol registry if they become hosts
    return when (typeA) {
        "number" -> a.unsafeCast<Double>().compareTo(b.unsafeCast<Double>())
        else -> a.toString().compareTo(b.toString())
    }
}
