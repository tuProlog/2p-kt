package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve

object GetEphemeral : AbstractGetData("ephemeral") {
    override val help: String =
        """
        `get_ephemeral(?Key, ?Value)`
        
        Reads term-valued entries from the ephemeral custom-data store of the current execution context. A variable `Key` enumerates available entries; an atom `Key` retrieves that entry when present.

        **Examples**

        ```prolog
        ?- set_ephemeral(k, v), get_ephemeral(k, X).
        X = v.

        % not kept across queries
        ?- get_ephemeral(k, X).
        no.

        ?- get_ephemeral(missing, X).
        no.
        ```
        """.trimIndent()

    override val Solve.Request<ExecutionContext>.data: Map<String, Any>
        get() = context.customData.ephemeral
}
