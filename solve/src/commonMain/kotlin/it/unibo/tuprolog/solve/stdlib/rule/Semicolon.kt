package it.unibo.tuprolog.solve.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.magic.MagicCut
import it.unibo.tuprolog.solve.stdlib.primitive.EnsureExecutable
import kotlin.collections.List as KtList
import kotlin.collections.listOf as ktListOf

sealed class Semicolon : RuleWrapper<ExecutionContext>(FUNCTOR, ARITY) {

    abstract override val Scope.head: KtList<Term>

    abstract override val Scope.body: Term

    sealed class If : Semicolon() {
        override val Scope.head: KtList<Term>
            get() = ktListOf(structOf(Arrow.functor, varOf("Cond"), varOf("Then")), varOf("Else"))

        object Then : If() {
            override val Scope.body: Term
                get() = tupleOf(
//                    structOf(EnsureExecutable.functor, varOf("Cond")),
                    structOf("call", varOf("Cond")),
                    MagicCut,
                    varOf("Then")
                )
        }

        object Else : If() {
            override val Scope.body: Term
                get() = tupleOf(
                    MagicCut,
//                    structOf(EnsureExecutable.functor, varOf("Else")),
                    varOf("Else")
                )
        }

    }

    sealed class Or : Semicolon() {
        override val Scope.head: KtList<Term>
            get() = ktListOf(varOf("A"), varOf("B"))

        object Left : Or() {
            override val Scope.body: Term
                get() = varOf("A")
        }

        object Right : Or() {
            override val Scope.body: Term
                get() = varOf("B")
        }
    }

    companion object {
        const val FUNCTOR: String = ";"
        const val ARITY: Int = 2
        val SIGNATURE: Signature
            get() = Signature(FUNCTOR, ARITY)
    }
}