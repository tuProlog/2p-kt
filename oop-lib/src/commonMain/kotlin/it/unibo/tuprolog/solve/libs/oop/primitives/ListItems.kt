package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.solve.libs.oop.OOPContext

class ListItems(oopContext: OOPContext) : AbstractIterableItems<List<*>>("list", List::class, oopContext) {
    override fun Sequence<Any?>.toIterable(): List<*> = toList()

    override val Any?.isIterable: Boolean
        get() = this is List<*>

    override val List<*>.items: Sequence<Any?>
        get() = asSequence()
}
