package it.unibo.tuprolog.core

class BlockIterator(
    block: Block,
) : Iterator<Term> {
    private open inner class BlockIteratorVisitor : TermVisitor<Term> {
        override fun visitTuple(term: Tuple): Term {
            current = term.right
            return term.left
        }

        override fun defaultValue(term: Term): Term {
            current = null
            return term
        }
    }

    private var current: Term? = block

    override fun hasNext(): Boolean = current.let { it != null && !it.isEmptyBlock }

    private val innerBlockIteratorVisitor = BlockIteratorVisitor()

    private val outerBlockIteratorVisitor =
        object : BlockIteratorVisitor() {
            override fun visitBlock(term: Block): Term = term[0].accept(innerBlockIteratorVisitor)

            override fun visitEmptyBlock(term: EmptyBlock): Term = throw throw NoSuchElementException()
        }

    override fun next(): Term = current?.accept(outerBlockIteratorVisitor) ?: throw NoSuchElementException()
}
