package it.unibo.tuprolog.core

/**
 * An [Iterator] walking the goals wrapped by a [Block], unfolding the [Tuple] its arguments are folded into
 * (if any), left to right.
 */
class BlockIterator(
    block: Block,
) : Iterator<Term> {
    private val delegate: Iterator<Term> =
        when (val item = block.args.firstOrNull()) {
            null -> emptyList<Term>().iterator()
            is Tuple -> TupleIterator(item)
            else -> listOf(item).iterator()
        }

    override fun hasNext(): Boolean = delegate.hasNext()

    override fun next(): Term = delegate.next()
}
