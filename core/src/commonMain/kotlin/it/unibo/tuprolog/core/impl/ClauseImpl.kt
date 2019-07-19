package it.unibo.tuprolog.core.impl

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

internal abstract class ClauseImpl(override val head: Struct?, override val body: Term)
    : StructImpl(Clause.FUNCTOR, (if (head === null) arrayOf(body) else arrayOf(head, body))), Clause {

    override val isWellFormed: Boolean by lazy {
        body.deepVisitNotableStructArgs({ bodyTerm -> bodyTerm !is Numeric }, Boolean::and)
    }

    override val functor: String
        get() = super<Clause>.functor

    override val args: Array<Term>
        get() = super<StructImpl>.args

    override fun toString(): String =
            when (head) {
                null -> "$functor $body"
                else -> "$head $functor $body"
            }

    companion object {

        /** Contains notable functor in determining if a Clause is well-formed */
        private val notableFunctorsForClause = listOf(",", ";", "->")

        /** Utility method to visit deeply Terms that have notable functor,
         * applying [visitorAction] and aggregating results with [resultAggregator] */
        private fun <VisitResult> Term.deepVisitNotableStructArgs(
                visitorAction: (Term) -> VisitResult,
                resultAggregator: (VisitResult, VisitResult) -> VisitResult
        ): VisitResult =
                when (this) {
                    is Struct -> when {
                        functor in notableFunctorsForClause && arity == 2 ->
                            this.argsSequence.map { arg ->
                                arg.deepVisitNotableStructArgs(visitorAction, resultAggregator)
                            }.reduce(resultAggregator)
                        else -> visitorAction(this)
                    }
                    else -> visitorAction(this)
                }
    }
}