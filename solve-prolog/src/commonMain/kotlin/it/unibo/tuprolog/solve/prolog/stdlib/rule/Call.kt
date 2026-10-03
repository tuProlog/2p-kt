package it.unibo.tuprolog.solve.prolog.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.prolog.PrologExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable
import kotlin.collections.List as KtList

/**
 * ISO `call/1`: `call(G) :- must_be_executable(G), G.` -- checks [it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable]
 * before proving `G` as a fresh, opaque-to-cut goal (a cut inside `G` only cuts choice points created while
 * proving `G` itself, since `call/1` becomes its own frame on the execution-context stack).
 */
object Call : RuleWrapper<PrologExecutionContext>("call", 1) {
    override val help: String =
        """
        `call(+Goal)`

        Calls `Goal`, which allows executing terms built or bound at runtime. `Goal` must be callable: a variable raises an instantiation error, a number or other non-callable term a type error (`callable`). `call/1` is opaque to cut: a `!` inside `Goal` only prunes the choice points created by `Goal` itself.
        """.trimIndent()

    override val Scope.head: KtList<Term>
        get() = listOf(varOf("G"))

    override val Scope.body: Term
        get() =
            tupleOf(
                structOf(EnsureExecutable.functor, varOf("G")),
                varOf("G"),
            )
}
