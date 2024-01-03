package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term

object SetOf : AbstractCollectionOf("setof") {
    override fun processSolutions(list: List<Term>): Iterable<Term> = LinkedHashSet(list)
}
