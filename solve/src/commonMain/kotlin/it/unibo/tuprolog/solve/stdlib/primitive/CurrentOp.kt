package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation

object CurrentOp : TernaryRelation.WithoutSideEffects<ExecutionContext>("current_op") {
    override val help: String =
        """
        `current_op(?Priority, ?Specifier, ?Name)`
        
        Enumerates the operators currently active in the solver, relating each operator with its numeric priority, specifier such as `yfx`, and functor name.

        **Examples**

        ```prolog
        ?- current_op(P, T, (mod)).
        P = 400, T = yfx.

        ?- current_op(P, xfy, ',').
        P = 1000.

        ?- current_op(1200, xfx, (:-)).
        yes.

        ?- current_op(P, T, no_such_op).
        no.
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term,
        third: Term,
    ): Sequence<Substitution> =
        context.operators
            .asSequence()
            .map {
                listOf(
                    mgu(first, Integer.of(it.priority)),
                    mgu(second, it.specifier.toTerm()),
                    mgu(third, Atom.of(it.functor)),
                )
            }.filter {
                it.all { sub -> sub is Substitution.Unifier }
            }.map { it.reduce(Substitution::plus) }
}
