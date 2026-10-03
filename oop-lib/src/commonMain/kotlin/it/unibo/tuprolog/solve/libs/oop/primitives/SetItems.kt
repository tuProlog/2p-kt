package it.unibo.tuprolog.solve.libs.oop.primitives

/**
 * `set_items(?SetRef, ?Items)`: converts between a Prolog list `Items` and an
 * [it.unibo.tuprolog.solve.libs.oop.ObjectRef] `SetRef` wrapping a Kotlin/Java `Set` holding the
 * same (converted, duplicate-free) elements, in either direction -- see [AbstractIterableItems].
 *
 * Example: `set_items(S, [1, 2, 2])` binds `S` to an [it.unibo.tuprolog.solve.libs.oop.ObjectRef]
 * wrapping `setOf(1, 2)`.
 */
object SetItems : AbstractIterableItems<Set<*>>("set", Set::class) {
    override val help: String =
        """
        `set_items(?SetRef, ?Items)`
        
        Converts between a Prolog list `Items` and an object reference `SetRef` wrapping a JVM/Kotlin `Set` with the same elements (duplicates are dropped, insertion order is kept). If `Items` is a list, each item is converted to an object (respecting `as` casts and `${'$'}Alias` references) and `SetRef` is unified with a reference to a fresh set; otherwise, if `SetRef` references a `Set`, `Items` is unified with its elements converted back to terms. Deterministic. Raises an instantiation error if both arguments are unbound, and a type error if `SetRef` is neither unbound nor an object reference; fails if it references something that is not a `Set`. Example: `set_items(S, [1, 2, 2]), set_items(S, L)` yields `L = [1, 2]`.
        """.trimIndent()

    override fun Sequence<Any?>.toIterable(): Set<*> = toSet()

    override val Any?.isIterable: Boolean
        get() = this is Set<*>

    override val Set<*>.items: Sequence<Any?>
        get() = asSequence()
}
