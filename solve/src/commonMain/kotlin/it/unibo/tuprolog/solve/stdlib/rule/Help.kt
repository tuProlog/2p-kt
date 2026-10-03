package it.unibo.tuprolog.solve.stdlib.rule

import it.unibo.tuprolog.core.Scope
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.rule.RuleWrapper
import it.unibo.tuprolog.solve.stdlib.primitive.InternalHelp
import kotlin.collections.List as KtList

/** Public entry point for solver documentation. */
object Help : RuleWrapper<ExecutionContext>("help", 2) {
    override val help: String =
        """
        `help(?Subject, -Help)`

        Retrieves Markdown documentation for a solver component.

        Supported subjects include predicate/function/operator indicators such as `functor/3`,
        flags such as `flag(unknown)`, and libraries such as `library('prolog.lang')`.

        The first argument may be left variable to enumerate documented subjects.

        **Examples**

        ```prolog
        ?- help(member/2, H), atom(H).
        yes.

        ?- help(flag(unknown), H), sub_atom(H, _, _, _, 'flag(unknown)').
        yes.

        ?- help(no_such_predicate/7, H).
        no.
        ```
        """.trimIndent()

    override val Scope.head: KtList<Term>
        get() = listOf(varOf("Subject"), varOf("Help"))

    override val Scope.body: Term
        get() = structOf(InternalHelp.functor, varOf("Subject"), varOf("Help"))
}
