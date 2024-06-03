package it.unibo.tuprolog.solve.libs.oop.primitives

import it.unibo.tuprolog.solve.libs.oop.OOPContext

class SetItems(oopContext: OOPContext) : AbstractIterableItems<Set<*>>("set", Set::class, oopContext) {
    override fun Sequence<Any?>.toIterable(): Set<*> = toSet()

    override val Any?.isIterable: Boolean
        get() = this is Set<*>

    override val Set<*>.items: Sequence<Any?>
        get() = asSequence()
}
