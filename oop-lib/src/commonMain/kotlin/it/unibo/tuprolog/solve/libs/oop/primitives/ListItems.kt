package it.unibo.tuprolog.solve.libs.oop.primitives

/**
 * `list_items(?ListRef, ?Items)`: converts between a Prolog list `Items` and an
 * [it.unibo.tuprolog.solve.libs.oop.ObjectRef] `ListRef` wrapping a Kotlin/Java `List` holding the
 * same (converted) elements, in either direction -- see [AbstractIterableItems].
 *
 * Example: `list_items(L, [1, 2, 3])` binds `L` to an [it.unibo.tuprolog.solve.libs.oop.ObjectRef]
 * wrapping `listOf(1, 2, 3)`.
 */
object ListItems : AbstractIterableItems<List<*>>("list", List::class) {
    override val help: String =
        """
        `list_items(?ListRef, ?Items)`
        
        Converts between a Prolog list `Items` and an object reference `ListRef` wrapping a JVM/Kotlin `List` with the same elements. If `Items` is a list, each item is converted to an object (respecting `as` casts and `${'$'}Alias` references) and `ListRef` is unified with a reference to a fresh list; otherwise, if `ListRef` references a `List`, `Items` is unified with its elements converted back to terms. Deterministic. Raises an instantiation error if both arguments are unbound, and a type error if `ListRef` is neither unbound nor an object reference; fails if it references something that is not a `List`. Example: `list_items(L, [a, b]), N := L.size` yields `N = 2`.
        """.trimIndent()

    override fun Sequence<Any?>.toIterable(): List<*> = toList()

    override val Any?.isIterable: Boolean
        get() = this is List<*>

    override val List<*>.items: Sequence<Any?>
        get() = asSequence()
}
