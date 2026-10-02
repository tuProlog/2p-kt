package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder

object SetPersistent : AbstractSetData("persistent") {
    override val help: String =
        """
        `set_persistent(+Key, ?Value)`
        
        Stores `Value` under atom `Key` in the persistent custom-data store of the current execution context.
        """.trimIndent()

    override fun SideEffectsBuilder.setData(
        key: String,
        value: Term,
    ) {
        setPersistentData(key, value)
    }
}
