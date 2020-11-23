package it.unibo.tuprolog.solve.libs.oop.primitives

object ArrayItems : AbstractIterableItems<Array<*>>("array", Array::class) {
    override fun Sequence<Any?>.toIterable(): Array<*> = toList().toTypedArray()

    override val Any?.isIterable: Boolean
        get() = this is Array<*>

    override val Array<*>.items: Sequence<Any?>
        get() = asSequence()
}
