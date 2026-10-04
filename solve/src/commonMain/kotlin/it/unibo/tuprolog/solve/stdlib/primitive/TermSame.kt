package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/** Implementation of '=@='/2 predicate */
object TermSame : BinaryRelation.Predicative<ExecutionContext>("=@=") {
    override val help: String =
        """
        `Left =@= Right`
        
        Succeeds when `Left` and `Right` compare as equal in the standard order of terms. It does not instantiate either term. In the current implementation a variable is only equal to itself, so variants such as `f(X)` and `f(Y)` are not considered the same.

        **Examples**

        ```prolog
        ?- f(a, X) =@= f(a, X).
        yes.

        ?- f(a) =@= f(b).
        no.

        % variants are not considered the same
        ?- f(X) =@= f(Y).
        no.
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.compute(
        first: Term,
        second: Term,
    ): Boolean = first.compareTo(second) == 0
}
