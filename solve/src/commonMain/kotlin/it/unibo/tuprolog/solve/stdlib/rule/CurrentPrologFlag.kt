package it.unibo.tuprolog.solve.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.CurrentFlag
import kotlin.collections.List as KtList

object CurrentPrologFlag : RuleWrapper<ExecutionContext>("current_prolog_flag", 2) {
    override val help: String =
        """
        `current_prolog_flag(?Name, ?Value)`
        
        Enumerates the flags currently present in the solver, relating each flag name with its current value. This is the public Prolog wrapper around the solver's flag store.

        **Examples**

        ```prolog
        ?- current_prolog_flag(unknown, V).
        V = warning.

        ?- current_prolog_flag(max_arity, V).
        V = 2147483647.

        ?- current_prolog_flag(no_such_flag, V).
        no.
        ```
        """.trimIndent()

    override val Scope.head: KtList<Term>
        get() = listOf(varOf("Key"), varOf("Value"))

    override val Scope.body: Term get() = structOf(CurrentFlag.functor, varOf("Key"), varOf("Value"))
}
