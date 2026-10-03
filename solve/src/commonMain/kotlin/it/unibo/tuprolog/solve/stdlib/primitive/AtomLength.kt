package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Atom
import it.unibo.tuprolog.core.Integer
import it.unibo.tuprolog.core.Substitution
import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.core.Var
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

object AtomLength : BinaryRelation.Functional<ExecutionContext>("atom_length") {
    override val help: String =
        """
        `atom_length(+Atom, ?Length)`
        
        Relates `Atom` with its character length. `Length` is a non-negative integer when supplied.

        **Examples**

        ```prolog
        ?- atom_length(hello, N).
        N = 5.

        ?- atom_length('', N).
        N = 0.

        ?- atom_length(X, 3).
        throws error(instantiation_error, _).

        ?- atom_length(123, N).
        throws error(type_error(atom, 123), _).
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOneSubstitution(
        first: Term,
        second: Term,
    ): Substitution =
        when {
            first is Var -> {
                ensuringAllArgumentsAreInstantiated()
                Substitution.failed()
            }
            second is Var -> {
                ensuringArgumentIsAtom(0)
                val atomLength = (first as Atom).value.length
                Substitution.of(second, Integer.of(atomLength))
            }
            else -> {
                ensuringArgumentIsAtom(0)
                ensuringArgumentIsNonNegativeInteger(1)
                val atomLength = Integer.of((first as Atom).value.length)
                mgu(atomLength, second)
            }
        }
}
