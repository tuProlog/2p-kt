package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve

object GetDurable : AbstractGetData("durable") {
    override val help: String =
        """
        `get_durable(?Key, ?Value)`
        
        Reads term-valued entries from the durable custom-data store of the current execution context. A variable `Key` enumerates available entries; an atom `Key` retrieves that entry when present.

        **Examples**

        ```prolog
        ?- set_durable(k, v), get_durable(k, X).
        X = v.

        % not kept across queries
        ?- get_durable(k, X).
        no.

        ?- get_durable(missing, X).
        no.
        ```
        """.trimIndent()

    override val Solve.Request<ExecutionContext>.data: Map<String, Any>
        get() = context.customData.durable
}
