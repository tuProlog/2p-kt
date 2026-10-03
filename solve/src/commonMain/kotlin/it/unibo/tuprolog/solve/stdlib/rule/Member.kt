package it.unibo.tuprolog.solve.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.Signature
import it.unibo.tuprolog.solve.rule.RuleWrapper
import kotlin.collections.List as KtList
import kotlin.collections.listOf as ktListOf

sealed class Member : RuleWrapper<ExecutionContext>(FUNCTOR, ARITY) {
    override val help: String =
        """
        `member(?Element, ?List)`
        
        Succeeds when `Element` is an element of `List`. On backtracking it enumerates matching elements; with suitable variables it can also generate list structures.

        **Examples**

        ```prolog
        ?- member(X, [a, b, c]).
        X = a ; X = b ; X = c.

        ?- member(b, [a, b, c]).
        yes.

        ?- member(d, [a, b, c]).
        no.
        ```
        """.trimIndent()

    abstract override val Scope.head: KtList<Term>

    object Base : Member() {
        override val Scope.head: KtList<Term>
            get() =
                ktListOf(
                    varOf("H"),
                    consOf(varOf("H"), whatever()),
                )
    }

    object Recursive : Member() {
        override val Scope.head: KtList<Term>
            get() =
                ktListOf(
                    varOf("H"),
                    consOf(whatever(), varOf("T")),
                )

        override val Scope.body: Term
            get() = structOf("member", varOf("H"), varOf("T"))
    }

    companion object {
        const val FUNCTOR: String = "member"
        const val ARITY: Int = 2
        val SIGNATURE: Signature
            get() = Signature(FUNCTOR, ARITY)
    }
}
