package it.unibo.tuprolog.solve.stdlib.primitive

import it.unibo.tuprolog.core.Term
import it.unibo.tuprolog.solve.sideffects.SideEffectsBuilder

object SetDurable : AbstractSetData("durable") {
    override fun SideEffectsBuilder.setData(
        key: String,
        value: Term,
    ) {
        setDurableData(key, value)
    }
}
