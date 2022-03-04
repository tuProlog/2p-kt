package it.unibo.tuprolog.solve.concurrent.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.magic.MagicCut
import it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable

sealed class NegationAsFailure : RuleWrapper<ExecutionContext>(FUNCTOR, ARITY) {

    override val Scope.head: List<Term>
        get() = ktListOf(
            varOf("X")
        )

    abstract override val Scope.body: Term

    object Fail : NegationAsFailure() {
        override val Scope.body: Term
            get() = tupleOf(
                structOf(EnsureExecutable.functor, varOf("X")),
                structOf(Call.functor, varOf("X")),
                MagicCut,
                truthOf(false)
            )
    }

    object Success : NegationAsFailure() {
        override val Scope.body: Term
            get() = truthOf(true)
    }

    companion object {
        const val FUNCTOR: String = "\\+"

        const val ARITY: Int = 1
    }
}
