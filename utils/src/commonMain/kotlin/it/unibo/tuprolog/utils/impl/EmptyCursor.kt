package it.unibo.tuprolog.utils.impl

internal object EmptyCursor : AbstractCursor<Nothing>() {
    override val next: AbstractCursor<Nothing>
        get() = throw NoSuchElementException()

    override val current: Nothing?
        get() = null

    override val hasNext: Boolean
        get() = false

    override val isOver: Boolean
        get() = true

    override fun toString(): String = super<AbstractCursor>.toString()

    override fun <R> map(mapper: (Nothing) -> R): AbstractCursor<out R> = this
}
