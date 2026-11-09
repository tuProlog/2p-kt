package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Clause
import it.unibo.tuprolog.core.Rule
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.error.PermissionError
import it.unibo.tuprolog.solve.primitive.Alternatives
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.UnaryPredicate

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

    // the next match is computed purely: the removal happens when the solver applies the response's side effects
    override val alternatives: Alternatives
        get() = Alternatives.LOOKAHEAD_SAFE

    override fun Solve.Request<ExecutionContext>.computeAll(first: Term): Sequence<Solve.Response> {
        ensuringArgumentIsWellFormedClause(0)
        val clause: Clause =
            when (first) {
                is Clause -> first
                is Struct -> Rule.of(first, Var.anonymous())
                else -> return sequenceOf(ensuringArgumentIsCallable(0).replyFail())
            }
        ensuringClauseProcedureHasPermission(clause, PermissionError.Operation.MODIFY)
        // one unification per candidate (#1019): for a head, unifying `head :- B` with the clause `H :- B` compares
        // whole clauses, as `Theory.get` does, at the cost of unifying heads, since unifying `B` with itself is free
        return context.dynamicKb.candidates(clause).mapNotNull { candidate ->
            val substitution =
                when (first) {
                    is Clause -> mgu(first, candidate)
                    else -> mgu(Rule.of(first.castToStruct(), candidate.body), candidate)
                }
            if (substitution.isSuccess) {
                replySuccess(substitution.castToUnifier()) {
                    removeDynamicClauses(candidate)
                }
            } else {
                null
            }
        }
    }
}
