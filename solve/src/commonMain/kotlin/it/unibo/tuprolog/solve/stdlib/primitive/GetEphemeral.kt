package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.solve.ExecutionContext
import it.unibo.tuprolog.solve.primitive.Solve

object GetEphemeral : AbstractGetData("ephemeral") {
    override val help: String =
        """
        `get_ephemeral(?Key, ?Value)`
        
        Reads term-valued entries from the ephemeral custom-data store of the current execution context. A variable `Key` enumerates available entries; an atom `Key` retrieves that entry when present.
        """.trimIndent()

    override val Solve.Request<ExecutionContext>.data: Map<String, Any>
        get() = context.customData.ephemeral
}
