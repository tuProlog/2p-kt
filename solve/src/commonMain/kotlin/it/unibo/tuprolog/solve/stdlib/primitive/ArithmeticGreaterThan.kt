package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.ArithmeticRelation

/** Implementation of '>'/2 predicate */
object ArithmeticGreaterThan : ArithmeticRelation<ExecutionContext>(">") {
    override val help: String =
        """
        `>(+Left, +Right)`
        
        Succeeds when the arithmetic value of `Left` is greater than the arithmetic value of `Right`.

        **Examples**

        ```prolog
        ?- 3 > 2.
        yes.

        ?- 2 * 2 > 5.
        no.

        ?- a > 1.
        throws error(type_error(evaluable, a/0), _).
        ```
        """.trimIndent()

    override fun computeNumeric(
        x: Numeric,
        y: Numeric,
    ): Boolean = x.compareValueTo(y) > 0
}
