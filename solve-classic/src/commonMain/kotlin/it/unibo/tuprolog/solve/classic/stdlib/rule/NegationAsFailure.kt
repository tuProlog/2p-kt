package it.unibo.tuprolog.solve.classic.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.magic.MagicCut
import it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable
import kotlin.collections.List as KtList

/**
 * ISO negation-as-failure `'\+'/1`, encoded as the two clauses of the classic Prolog idiom
 * `\+ X :- must_be_executable(X), call(X), !, fail. \+ X :- true.`: [Fail] tries `X`, and if it succeeds at all,
 * cuts (via [it.unibo.tuprolog.solve.stdlib.magic.MagicCut], since an ordinary `!` here would only be
 * transparent up to this clause, not back to the caller that invoked `\+`) and fails; [Success] is only ever
 * reached, via backtracking into the second clause, when [Fail]'s `X` had no solution at all. Both
 * implementations are registered together (see [it.unibo.tuprolog.solve.classic.stdlib.DefaultBuiltins]) so
 * that ordinary clause selection on `\+/1` finds exactly these two clauses, in order.
 */
sealed class NegationAsFailure : RuleWrapper<ExecutionContext>(FUNCTOR, ARITY) {
    override val Scope.head: KtList<Term>
        get() = listOf(varOf("X"))

    abstract override val Scope.body: Term

    /** The first clause: prove `X`, and on success cut back to the caller of `\+` and fail. */
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

    /** The fallback clause: reached only if [Fail]'s `X` failed outright, so `\+ X` succeeds. */
    object Success : NegationAsFailure() {
        override val Scope.body: Term
            get() = truthOf(true)
    }

    companion object {
        const val FUNCTOR: String = "\\+"

        const val ARITY: Int = 1
    }
}
