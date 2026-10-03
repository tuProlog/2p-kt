package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Numeric
import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.ArithmeticRelation

/** Implementation of '<'/2 predicate */
object ArithmeticLowerThan : ArithmeticRelation<ExecutionContext>("<") {
    override val help: String =
        """
        `<(+Left, +Right)`
        
        Succeeds when the arithmetic value of `Left` is less than the arithmetic value of `Right`.

        **Examples**

        ```prolog
        ?- 1 < 2.
        yes.

        ?- 2 < 1 + 1.
        no.

        ?- X < 1.
        throws error(instantiation_error, _).
        ```
        """.trimIndent()

    override fun computeNumeric(
        x: Numeric,
        y: Numeric,
    ): Boolean = x.compareValueTo(y) < 0
}
