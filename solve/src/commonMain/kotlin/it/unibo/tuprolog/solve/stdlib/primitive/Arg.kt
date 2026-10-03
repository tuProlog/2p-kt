package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Struct
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve
import it.unibo.tuprolog.solve.primitive.TernaryRelation
import org.gciatto.kt.math.BigInteger

object Arg : TernaryRelation.WithoutSideEffects<ExecutionContext>("arg") {
    override val help: String =
        """
        `arg(?Index, +Term, ?Argument)`
        
        Relates a compound `Term` with one of its arguments, using one-based indexing. If `Index` is a variable, solutions enumerate argument positions; otherwise `Index` must be an integer. `Term` must be instantiated and compound.

        **Examples**

        ```prolog
        ?- arg(2, f(a, b, c), X).
        X = b.

        ?- arg(N, f(a, b), X).
        N = 1, X = a ; N = 2, X = b.

        ?- arg(4, f(a, b, c), X).
        no.

        ?- arg(a, f(a), X).
        throws error(type_error(integer, a), _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeAllSubstitutions(
        first: Term,
        second: Term,
        third: Term,
    ): Sequence<Substitution> =
        ensuringArgumentIsInstantiated(1)
            .ensuringArgumentIsCompound(1)
            .run {
                val compound = second as Struct
                return when (first) {
                    is Var -> {
                        compound.argsSequence
                            .mapIndexed { i, arg ->
                                (i + 1) to mgu(arg, third)
                            }.filter { (_, sub) ->
                                sub is Substitution.Unifier
                            }.map { (i, sub) ->
                                sub + Substitution.of(first to Integer.of(i))
                            }
                    }
                    is Integer -> {
                        ensuringArgumentIsNonNegativeInteger(0)
                        if (first.value in BigInteger.ONE..BigInteger.of(compound.arity)) {
                            sequenceOf(mgu(third, compound[first.value.toInt() - 1]))
                        } else {
                            sequenceOf(Substitution.failed())
                        }
                    }
                    else -> {
                        ensuringArgumentIsInteger(0)
                        sequenceOf(Substitution.failed())
                    }
                }
            }
}
