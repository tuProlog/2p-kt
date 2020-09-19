package it.unibo.tuprolog.solve.libs.oop.primitives

object SetItems : AbstractIterableItems<Set<*>>("set", Set::class) {
    override fun Sequence<Any?>.toIterable(): Set<*> = toSet()

    override val Any?.isIterable: Boolean
        get() = this is Set<*>

    override val Set<*>.items: Sequence<Any?>
        get() = asSequence()
}
