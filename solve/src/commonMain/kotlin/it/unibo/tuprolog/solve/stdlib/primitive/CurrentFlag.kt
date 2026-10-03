package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

object CurrentFlag : BinaryRelation.WithoutSideEffects<ExecutionContext>("current_flag") {
    override val help: String =
        """
        `current_flag(?Name, ?Value)`
        
        Enumerates the flags currently present in the solver's flag store, relating each atom `Name` with its current `Value`.

        **Examples**

        ```prolog
        ?- current_flag(max_arity, X).
        X = 2147483647.

        ?- current_flag(unknown, X).
        X = warning.

        ?- current_flag(no_such_flag, X).
        no.
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term,
    ): Sequence<Substitution> =
        when (first) {
            is Atom, is Var -> {
                context.flags
                    .asSequence()
                    .map { (k, v) -> Atom.of(k) to v }
                    .map { (k, v) -> mgu(k, first) + mgu(v, second) }
                    .filter { it.isSuccess }
            }
            else -> {
                ensuringArgumentIsAtom(0)
                emptySequence()
            }
        }
}
