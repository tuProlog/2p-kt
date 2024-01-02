package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder

object SetEphemeral : AbstractSetData("ephemeral") {
    override fun SideEffectsBuilder.setData(
        key: String,
        value: Term,
    ) {
        setEphemeralData(key, value)
    }
}
