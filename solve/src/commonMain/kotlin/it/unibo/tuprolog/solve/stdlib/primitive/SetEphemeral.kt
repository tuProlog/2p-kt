package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder

object SetEphemeral : AbstractSetData("ephemeral") {
    override val help: String =
        """
        `set_ephemeral(+Key, ?Value)`
        
        Stores `Value` under atom `Key` in the ephemeral custom-data store of the current execution context.
        """.trimIndent()

    override fun SideEffectsBuilder.setData(
        key: String,
        value: Term,
    ) {
        setEphemeralData(key, value)
    }
}
