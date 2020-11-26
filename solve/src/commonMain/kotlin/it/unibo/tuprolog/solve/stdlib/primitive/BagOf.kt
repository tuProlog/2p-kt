package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term

object BagOf : AbstractCollectionOf("bagof") {
    override fun processSolutions(list: List<Term>): Iterable<Term> = list
}
