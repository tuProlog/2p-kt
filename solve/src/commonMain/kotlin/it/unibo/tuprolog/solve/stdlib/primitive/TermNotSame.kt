package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.BinaryRelation
import it.unibo.tuprolog.solve.primitive.Solve

/** Implementation of '\=@='/2 predicate */
object TermNotSame : BinaryRelation.Predicative<ExecutionContext>("\\=@=") {
    override val help: String =
        """
        `Left \=@= Right`
        
        Succeeds when `Left` and `Right` do not compare as equal in the standard order of terms. In the current implementation a variable is only equal to itself, so this also succeeds for variants such as `f(X)` and `f(Y)`.

        **Examples**

        ```prolog
        % `\=@=` is not a default operator, so canonical notation is used
        ?- \=@=(f(a), f(b)).
        yes.

        ?- \=@=(f(X), f(X)).
        no.

        % variants are not considered the same
        ?- \=@=(f(X), f(Y)).
        yes.
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.compute(
        first: Term,
        second: Term,
    ): Boolean = first.compareTo(second) != 0
}
