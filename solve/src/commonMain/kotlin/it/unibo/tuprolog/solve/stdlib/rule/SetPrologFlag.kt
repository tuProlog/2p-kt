package it.unibo.tuprolog.solve.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.SetFlag
import kotlin.collections.List as KtList

object SetPrologFlag : RuleWrapper<ExecutionContext>("set_prolog_flag", 2) {
    override val help: String =
        """
        `set_prolog_flag(+Name, +Value)`
        
        Sets a Prolog flag in the current solver. Notable flags enforce their editability and admissible values; invalid assignments raise the corresponding flag error.

        **Examples**

        ```prolog
        ?- set_prolog_flag(unknown, fail), current_prolog_flag(unknown, V).
        V = fail.

        ?- set_prolog_flag(unknown, maybe).
        throws error(domain_error(_, maybe), _).

        ?- set_prolog_flag(max_arity, 10).
        throws error(permission_error(modify, flag, max_arity), _).
        ```
        """.trimIndent()

    override val Scope.head: KtList<Term>
        get() = listOf(varOf("Key"), varOf("Value"))

    override val Scope.body: Term get() = structOf(SetFlag.functor, varOf("Key"), varOf("Value"))
}
