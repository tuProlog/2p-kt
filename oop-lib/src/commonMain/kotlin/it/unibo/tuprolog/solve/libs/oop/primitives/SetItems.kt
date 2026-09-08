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
    override fun Sequence<Any?>.toIterable(): Set<*> = toSet()

    override val Any?.isIterable: Boolean
        get() = this is Set<*>

    override val Set<*>.items: Sequence<Any?>
        get() = asSequence()
}
