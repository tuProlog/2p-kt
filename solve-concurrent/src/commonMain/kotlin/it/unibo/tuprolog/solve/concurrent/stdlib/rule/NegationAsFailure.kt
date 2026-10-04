package it.unibo.tuprolog.solve.concurrent.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.magic.MagicCut
import it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable

/**
 * A rule-based, two-clause implementation of negation as failure (`\+/1`): [Fail] tries `X` and, if it succeeds,
 * cuts and fails; [Success] is the fallback clause that succeeds when [Fail] didn't. This is the same encoding
 * `:solve-prolog` uses, but here it is __shadowed__ by the primitive
 * [it.unibo.tuprolog.solve.concurrent.stdlib.primitive.Naf] registered under the same `\+/1` signature in
 * [it.unibo.tuprolog.solve.concurrent.stdlib.DefaultBuiltins]: primitives are looked up before rules (see
 * [it.unibo.tuprolog.solve.concurrent.fsm.StatePrimitiveSelection]), so [Fail]/[Success] never actually run as
 * long as that primitive stays registered.
 */
sealed class NegationAsFailure : RuleWrapper<ExecutionContext>(FUNCTOR, ARITY) {
    override val help: String =
        """
        `\+ +Goal`

        Negation as failure: succeeds if `Goal` has no solution, and fails as soon as `Goal` has one. It never binds the variables of `Goal`, and is equivalent to `not/1`. `Goal` is proved by an independent sub-solver; errors it raises are propagated. `Goal` must be callable, otherwise an instantiation or type error is raised.

        **Examples**

        ```prolog
        ?- \+ member(d, [a, b, c]).
        yes.

        ?- \+ X = 1.
        no.

        ?- \+ (X = 1, X = 2), var(X).
        yes.

        ?- \+ 3.
        throws error(type_error(callable, 3), _).
        ```
        """.trimIndent()

    override val Scope.head: List<Term>
        get() = listOf(varOf("X"))

    abstract override val Scope.body: Term

    /** `\+(X) :- ensure_executable(X), call(X), !, fail.` -- fails if `X` succeeds at least once. */
    object Fail : NegationAsFailure() {
        override val Scope.body: Term
            get() =
                tupleOf(
                    structOf(EnsureExecutable.functor, varOf("X")),
                    structOf(Call.functor, varOf("X")),
                    MagicCut,
                    truthOf(false),
                )
    }

    /** `\+(X) :- true.` -- the fallback clause, reached only if [Fail] didn't cut resolution short. */
    object Success : NegationAsFailure() {
        override val Scope.body: Term
            get() = truthOf(true)
    }

    companion object {
        /** The functor both [Fail] and [Success] are registered under: `\+`. */
        const val FUNCTOR: String = "\\+"

        /** The arity both [Fail] and [Success] are registered under: `1`. */
        const val ARITY: Int = 1
    }
}
