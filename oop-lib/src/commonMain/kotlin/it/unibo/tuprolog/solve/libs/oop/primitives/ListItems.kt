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
    override fun Sequence<Any?>.toIterable(): List<*> = toList()

    override val Any?.isIterable: Boolean
        get() = this is List<*>

    override val List<*>.items: Sequence<Any?>
        get() = asSequence()
}
