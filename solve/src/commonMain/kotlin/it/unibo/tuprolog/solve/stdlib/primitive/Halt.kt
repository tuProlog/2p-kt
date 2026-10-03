package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.exception.HaltException
import it.unibo.tuprolog.solve.primitive.PredicateWithoutArguments
import it.unibo.tuprolog.solve.primitive.Solve

/**
 * Implementation of primitive handling `halt/0` behaviour
 *
 * @author Enrico
 */
object Halt : PredicateWithoutArguments.NonBacktrackable<ExecutionContext>("halt") {
    override val help: String =
        """
        `halt`
        
        Stops the current Prolog computation by raising a halt condition with the default exit status.

        **Examples**

        ```prolog
        % stops the computation: nothing after halt is run
        ?- halt.
        yes.
        ```
        """.trimIndent()

    override fun Solve.Request<ExecutionContext>.computeOne(): Solve.Response = throw HaltException(context = context)
}
