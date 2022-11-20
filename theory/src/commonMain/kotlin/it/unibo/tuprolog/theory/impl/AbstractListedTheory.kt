package it.unibo.tuprolog.theory.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.theory.AbstractTheory
import it.unibo.tuprolog.unify.Unificator
import kotlin.collections.List as KtList

internal abstract class AbstractListedTheory protected constructor(
    override val unificator: Unificator,
    override val clauses: KtList<Clause>,
    tags: Map<String, Any>
) : AbstractTheory(tags) {

    final override fun get(clause: Clause): Sequence<Clause> =
        clauses.filter {
            unificator.match(it, clause)
        }.asSequence()
}
