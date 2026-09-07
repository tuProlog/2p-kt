package it.unibo.tuprolog.solve.libs.oop.primitives

/**
 * `array_items(?ArrayRef, ?Items)`: converts between a Prolog list `Items` and an
 * [it.unibo.tuprolog.solve.libs.oop.ObjectRef] `ArrayRef` wrapping a JVM `Object[]` array holding
 * the same (converted) elements, in either direction -- see [AbstractIterableItems].
 *
 * Example: `array_items(A, [1, 2, 3])` binds `A` to an [it.unibo.tuprolog.solve.libs.oop.ObjectRef]
 * wrapping `new Object[]{1, 2, 3}`.
 */
object ArrayItems : AbstractIterableItems<Array<*>>("array", Array::class) {
    override fun Sequence<Any?>.toIterable(): Array<*> = toList().toTypedArray()

    override val Any?.isIterable: Boolean
        get() = this is Array<*>

    override val Array<*>.items: Sequence<Any?>
        get() = asSequence()
}
