package it.unibo.tuprolog.solve.libs.oop.primitives

object ListItems : AbstractIterableItems<List<*>>("list", List::class) {
    override fun Sequence<Any?>.toIterable(): List<*> = toList()

    override val Any?.isIterable: Boolean
        get() = this is List<*>

    override val List<*>.items: Sequence<Any?>
        get() = asSequence()
}
