package it.unibo.tuprolog.solve.concurrent.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.concurrent.ConcurrentExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable

/**
 * `call/1`: checks (via `it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable`) that its argument is a
 * callable term, then resolves it as a goal -- equivalent to `call(G) :- ensure_executable(G), G.`.
 */
object Call : RuleWrapper<ConcurrentExecutionContext>("call", 1) {
    override val help: String =
        """
        `call(+Goal)`

        Calls `Goal`, which allows executing terms built or bound at runtime. `Goal` must be callable: a variable raises an instantiation error, a number or other non-callable term a type error (`callable`).

        **Examples**

        ```prolog
        % solutions may come in any order: collect them to compare
        % solutions may come in any order on this engine
        ?- G = member(X, [a, b]), findall(X, call(G), L), member(a, L), member(b, L).
        yes.

        ?- call(1).
        throws error(type_error(callable, 1), _).
        ```
        """.trimIndent()

    override val Scope.head: List<Term>
        get() = listOf(varOf("G"))

    override val Scope.body: Term
        get() =
            tupleOf(
                structOf(EnsureExecutable.functor, varOf("G")),
                varOf("G"),
            )
}
