package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder

object SetDurable : AbstractSetData("durable") {
    override val help: String =
        """
        `set_durable(+Key, ?Value)`
        
        Stores `Value` under atom `Key` in the durable custom-data store of the current execution context.
        """.trimIndent()

    override fun SideEffectsBuilder.setData(
        key: String,
        value: Term,
    ) {
        setDurableData(key, value)
    }
}
