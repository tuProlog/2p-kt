package it.unibo.tuprolog.solve.classic.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.classic.ClassicExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable
import kotlin.collections.List as KtList

/**
 * ISO `call/1`: `call(G) :- must_be_executable(G), G.` -- checks [it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable]
 * before proving `G` as a fresh, opaque-to-cut goal (a cut inside `G` only cuts choice points created while
 * proving `G` itself, since `call/1` becomes its own frame on the execution-context stack).
 */
object Call : RuleWrapper<ClassicExecutionContext>("call", 1) {
    override val Scope.head: KtList<Term>
        get() = listOf(varOf("G"))

    override val Scope.body: Term
        get() =
            tupleOf(
                structOf(EnsureExecutable.functor, varOf("G")),
                varOf("G"),
            )
}
