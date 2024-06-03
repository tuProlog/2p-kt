package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.solve.libs.oop.OOPContext

class ArrayItems(oopContext: OOPContext) : AbstractIterableItems<Array<*>>("array", Array::class, oopContext) {
    override fun Sequence<Any?>.toIterable(): Array<*> = toList().toTypedArray()

    override val Any?.isIterable: Boolean
        get() = this is Array<*>

    override val Array<*>.items: Sequence<Any?>
        get() = asSequence()
}
