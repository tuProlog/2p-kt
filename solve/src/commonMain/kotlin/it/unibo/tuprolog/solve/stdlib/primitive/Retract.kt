package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate
import it.unibo.tuprolog.utils.buffered

object Retract : UnaryPredicate<ExecutionContext>("retract") {
    override val help: String =
        """
        `retract(+Clause)`
        
        Removes one matching clause from the dynamic knowledge base and succeeds once for each removable matching clause on backtracking.

        **Examples**

        ```prolog
        ?- assertz(p(1)), assertz(p(2)), assertz(p(3)).
        yes.

        ?- retract(p(X)).
        X = 1.

        ?- findall(X, p(X), L).
        L = [2, 3].

        ?- retract(p(4)).
        no.
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeAll(first: Term): Sequence<Solve.Response> {
        ensuringArgumentIsWellFormedClause(0)
        val clause: Clause =
            when (first) {
                is Clause -> first
                is Struct -> Rule.of(first, Var.anonymous())
                else -> return sequenceOf(ensuringArgumentIsCallable(0).replyFail())
            }
        ensuringClauseProcedureHasPermission(clause, PermissionError.Operation.MODIFY)
        return context.dynamicKb[clause].buffered().map {
            val substitution =
                when (first) {
                    is Clause -> mgu(first, it) as Substitution.Unifier
                    else -> mgu(first, it.head!!) as Substitution.Unifier
                }
            replySuccess(substitution) {
                removeDynamicClauses(it)
            }
        }
    }
}
