package it.unibo.tuprolog.datalog.visitors

import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term

/**
 * A [AbstractClauseVisitor] that detects whether a clause contains any compound term (a [Struct] with
 * [Struct.arity] `> 0`) as an argument, anywhere in its head or body literals — the check backing
 * [it.unibo.tuprolog.datalog.hasNoCompound]. Any non-[Struct] argument (a number, a variable, ...) is
 * ignored ([defaultValue]).
 *
 * `clause.accept(CompoundFinder)` is `true` as soon as *any* argument, at any nesting depth, is itself a
 * [Struct] with one or more arguments — e.g. `p(f(X))` is flagged because of `f(X)`, while `p(a, X)` is not.
 */
object CompoundFinder : AbstractClauseVisitor<Boolean>() {
    override fun reduce(results: Sequence<Boolean>): Boolean = results.any { it }

    override fun defaultValue(term: Term): Boolean = false

    override fun visitStruct(term: Struct): Boolean = term.arity > 0
}
